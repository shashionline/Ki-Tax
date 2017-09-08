package ch.dvbern.ebegu.api.resource.auth;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.EJBAccessException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.dvbern.ebegu.api.connector.ILoginConnectorResource;
import ch.dvbern.ebegu.api.dtos.JaxExternalAuthAccessElement;
import ch.dvbern.ebegu.api.dtos.JaxExternalAuthorisierterBenutzer;
import ch.dvbern.ebegu.api.dtos.JaxExternalBenutzer;
import ch.dvbern.ebegu.api.dtos.JaxMandant;
import ch.dvbern.ebegu.api.resource.InstitutionResource;
import ch.dvbern.ebegu.api.resource.MandantResource;
import ch.dvbern.ebegu.api.resource.TraegerschaftResource;
import ch.dvbern.ebegu.api.util.version.VersionInfoBean;
import ch.dvbern.ebegu.authentication.AuthAccessElement;
import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.entities.AuthorisierterBenutzer;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.services.AuthService;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.MandantService;
import ch.dvbern.ebegu.services.TraegerschaftService;

import static ch.dvbern.ebegu.enums.UserRole.SACHBEARBEITER_INSTITUTION;

/**
 * Service provided by KI-TAX to allow an external login module to create users and logins
 */
@Stateless
public class LoginConnectorResource implements ILoginConnectorResource {

	private final Logger LOG = LoggerFactory.getLogger(LoginConnectorResource.class.getSimpleName());

	private final BenutzerService benutzerService;
	private final AuthService authService;

	private final VersionInfoBean versionInfoBean;

	private final InstitutionService institutionService;
	private final InstitutionResource institutionResource;
	private final TraegerschaftService traegerschaftService;
	private final TraegerschaftResource traegerschaftResource;
	private final MandantResource mandantResource;
	private final MandantService mandantService;
	private final LocalhostChecker localhostChecker;
	private final EbeguConfiguration configuration;

	@Context
	private HttpServletRequest request;

	@Context
	private UriInfo uriInfo;


	@Inject
	public LoginConnectorResource(
		VersionInfoBean versionInfoBean,
		LocalhostChecker localhostChecker,
		EbeguConfiguration configuration,
		BenutzerService benutzerService,
		AuthService authService,
		InstitutionResource institutionResource,
		InstitutionService institutionService,
		TraegerschaftResource traegerschaftResource,
		TraegerschaftService traegerschaftService,
		MandantResource mandantResource,
		MandantService mandantService) {

		this.configuration = configuration;
		this.versionInfoBean = versionInfoBean;
		this.localhostChecker = localhostChecker;
		this.benutzerService = benutzerService;
		this.authService = authService;
		this.institutionResource = institutionResource;
		this.institutionService = institutionService;
		this.traegerschaftResource = traegerschaftResource;
		this.traegerschaftService = traegerschaftService;
		this.mandantResource = mandantResource;
		this.mandantService = mandantService;
	}


	@Override
	public String getHeartBeat() {
		StringBuilder builder = new StringBuilder();
		if (versionInfoBean != null && versionInfoBean.getVersionInfo().isPresent()) {
			builder.append("Version: ");
			builder.append(versionInfoBean.getVersionInfo().get().getVersion());

		} else {
			builder.append("unknown Version");
		}
		final boolean isAccessedLocally = localhostChecker.isAddressLocalhost(request.getRemoteAddr());
		if (!isAccessedLocally) {
			builder.append(" WARNING access is not local");
		}
		return builder.toString();

	}

	@Override
	public JaxExternalBenutzer updateOrStoreUserFromIAM(@Nonnull JaxExternalBenutzer benutzer) {
		LOG.debug("Requested url {} ", this.uriInfo.getAbsolutePath());
		LOG.debug("Requested forwared for {} ", this.request.getHeader("X-Forwarded-For"));
		checkLocalAccessOnly();

		Benutzer user = new Benutzer();
		user.setUsername(benutzer.getUsername());
		user.setEmail(benutzer.getEmail());
		user.setNachname(benutzer.getNachname());
		user.setVorname(benutzer.getVorname());
		user.setRole(convertRoleString(benutzer.getRole()));

		String unusedAttr = StringUtils.join(benutzer.getCommonName(), benutzer.getTelephoneNumber(),
			benutzer.getMobile(), benutzer.getPreferredLang(), benutzer.getPostalCode(), benutzer.getState(),
			benutzer.getStreet(), benutzer.getPostalCode(), benutzer.getCountryCode(), benutzer.getCountry(),
			benutzer.getCountry(), ',');

		LOG.warn("The following attributes are received from the ExternalLoginModule but not yet stored {}", unusedAttr);

		Mandant mandant = this.mandantService.findMandant(benutzer.getMandantId())
			.orElseThrow(() -> {
				LOG.error("Mandant not found for passed id: {}", benutzer.getMandantId());
				return new EbeguEntityNotFoundException("updateOrStoreMandant", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND);
			});
		user.setMandant(mandant);

		if (SACHBEARBEITER_INSTITUTION == convertRoleString(benutzer.getRole())) {
			Institution instFromDB = this.institutionService.findInstitution(benutzer.getInstitutionId()).orElseThrow(() -> {
				LOG.error("Institution not found for passed id: '{}' that was received in Benutzer from externalLoginModul",
					benutzer.getInstitutionId());
				return new EbeguEntityNotFoundException("updateOrStoreUserFromIAM", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND);
			});
			user.setInstitution(instFromDB);
		}

		if (UserRole.SACHBEARBEITER_TRAEGERSCHAFT == convertRoleString(benutzer.getRole())) {

			Traegerschaft traegerschaftFromDB = this.traegerschaftService.findTraegerschaft(benutzer.getTraegerschaftId()).orElseThrow(() -> {
				LOG.error("Traegerschaft not found for passed id: '{}' that was received in Benutzer from externalLoginModul",
					benutzer.getTraegerschaftId());
				return new EbeguEntityNotFoundException("updateOrStoreUserFromIAM", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND);
			});
			user.setTraegerschaft(traegerschaftFromDB);
		}

		Benutzer storedUser = benutzerService.updateOrStoreUserFromIAM(user);
		return convertBenuterToJax(storedUser);
	}

	private JaxExternalBenutzer convertBenuterToJax(Benutzer storedUser) {
		JaxExternalBenutzer jaxExternalBenutzer = new JaxExternalBenutzer();
		jaxExternalBenutzer.setUsername(storedUser.getUsername());
		jaxExternalBenutzer.setEmail(storedUser.getEmail());
		jaxExternalBenutzer.setNachname(storedUser.getNachname());
		jaxExternalBenutzer.setVorname(storedUser.getVorname());
		jaxExternalBenutzer.setRole(storedUser.getRole() != null ? storedUser.getRole().name() : null);
		jaxExternalBenutzer.setMandantId(storedUser.getMandant().getId());
		if (storedUser.getInstitution() != null) {
			jaxExternalBenutzer.setInstitutionId(storedUser.getInstitution().getId());
		}
		if (storedUser.getTraegerschaft() != null) {
			jaxExternalBenutzer.setTraegerschaftId(storedUser.getTraegerschaft().getId());
		}
		return jaxExternalBenutzer;

	}


	@Nonnull
	@Override
	public String getMandant() {
		final JaxMandant first = mandantResource.getFirst();
		if (first.getId() == null) {
			LOG.error("error while loading mandant");
			throw new EbeguEntityNotFoundException("getFirst", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND);
		} else{
			return first.getId();
		}
	}

	@Override
	public JaxExternalAuthAccessElement createLoginFromIAM(@Nonnull JaxExternalAuthorisierterBenutzer jaxExtAuthUser) {
		Validate.notNull(jaxExtAuthUser, "Passed JaxExternalAuthorisierterBenutzer may not be null");

		LOG.debug("ExternalLogin System is creating Authorization for user " + jaxExtAuthUser.getUsername());
		LOG.debug("Requested url {} ", this.uriInfo.getAbsolutePath());

		checkLocalAccessOnly();

		AuthorisierterBenutzer authUser = convertExternalLogin(jaxExtAuthUser);
		AuthAccessElement loginDataForCookie = this.authService.createLoginFromIAM(authUser);
		return convertToJaxExternalAuthAccessElement(loginDataForCookie);
	}

	@Nonnull
	private JaxExternalAuthAccessElement convertToJaxExternalAuthAccessElement(@Nonnull AuthAccessElement loginDataForCookie) {
		Validate.notNull(loginDataForCookie, "login data to convert may not be null");
		return new JaxExternalAuthAccessElement(
			loginDataForCookie.getAuthId(),
			loginDataForCookie.getAuthToken(),
			loginDataForCookie.getXsrfToken(),
			loginDataForCookie.getNachname(),
			loginDataForCookie.getVorname(),
			loginDataForCookie.getEmail(),
			loginDataForCookie.getRole().name()
		);

	}

	/**
	 * currently we allow requests to this services only from localhost
	 */
	private void checkLocalAccessOnly() {
		if (!this.configuration.isRemoteLoginConnectorAllowed()) {

			boolean isLocallyAccessed = this.localhostChecker.isAddressLocalhost(request.getRemoteAddr());
			final String requestedHost = this.request.getHeader("host");
			String hostmachine = requestedHost != null ? requestedHost.split(":")[0] : "";
			if (!isLocallyAccessed) {
				LOG.error("Refusing remote access for host {} from remote addr {} ", hostmachine, request.getRemoteAddr());
				throw new EJBAccessException("This Service may only be called from localhost but was accessed from  " + request.getRemoteAddr());
			}
		}
	}

	@Nonnull
	private AuthorisierterBenutzer convertExternalLogin(JaxExternalAuthorisierterBenutzer jaxExtAuthBen) {
		AuthorisierterBenutzer authUser = new AuthorisierterBenutzer();
		authUser.setUsername(jaxExtAuthBen.getUsername());
		authUser.setAuthToken(jaxExtAuthBen.getAuthToken());
		authUser.setLastLogin(jaxExtAuthBen.getLastLogin());
		authUser.setRole(convertRoleString(jaxExtAuthBen.getRole()));
		authUser.setSamlIDPEntityID(jaxExtAuthBen.getSamlIDPEntityID());
		authUser.setSamlSPEntityID(jaxExtAuthBen.getSamlSPEntityID());
		authUser.setSamlNameId(jaxExtAuthBen.getSamlNameId());
		authUser.setSessionIndex(jaxExtAuthBen.getSessionIndex());
		return authUser;
	}

	@Nullable
	private UserRole convertRoleString(@Nullable String roleString) {
		if (roleString == null) {
			return null;
		}
		try {
			return UserRole.valueOf(roleString);
		} catch (IllegalArgumentException e) {
			LOG.error("Invalid Value for role, Could not convert {} to a valid UserRole", roleString);
			throw e;
		}
	}
}
