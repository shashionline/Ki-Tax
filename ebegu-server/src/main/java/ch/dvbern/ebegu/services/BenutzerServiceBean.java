package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Benutzer_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.ISessionContextService;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Benutzer
 */
@Stateless
@Local(BenutzerService.class)
public class BenutzerServiceBean extends AbstractBaseService implements BenutzerService {

	@Inject
	private Persistence<Benutzer> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private ISessionContextService sessionContext;


	@Nonnull
	@Override
	public Benutzer saveBenutzer(@Nonnull Benutzer benutzer) {
		Objects.requireNonNull(benutzer);
		return persistence.merge(benutzer);
	}

	@Nonnull
	@Override
	public Optional<Benutzer> findBenutzer(@Nonnull String username) {
		Objects.requireNonNull(username, "username muss gesetzt sein");
		return criteriaQueryHelper.getEntityByUniqueAttribute(Benutzer.class, username, Benutzer_.username);
	}

	@Nonnull
	@Override
	public Collection<Benutzer> getAllBenutzer() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Benutzer.class));
	}

	@Override
	public void removeBenutzer(@Nonnull String username) {
		Objects.requireNonNull(username);
		Optional<Benutzer> benutzerToRemove = findBenutzer(username);
		benutzerToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeBenutzer", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, username));
		persistence.remove(benutzerToRemove.get());
	}

	@Nonnull
	@Override
	public Optional<Benutzer> getCurrentBenutzer() {
		String username = null;
		if (sessionContext != null) {
			final Principal principal = sessionContext.getCallerPrincipal();
			if (principal != null) {
				username = principal.getName();
			}
		}
		if (StringUtils.isNotEmpty(username)) {
			return findBenutzer(username);
		}
		return Optional.empty();
	}

	@Override
	public Benutzer updateOrStoreUserFromIAM(Benutzer benutzer) {
		Optional<Benutzer> foundUser = this.findBenutzer(benutzer.getUsername());
		if (foundUser.isPresent()) {
			benutzer.setId(foundUser.get().getId());
			benutzer.setVersion(foundUser.get().getVersion()); //hack fuer ol
		}
		return this.saveBenutzer(benutzer);
	}
}
