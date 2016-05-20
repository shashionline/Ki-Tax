package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxBetreuung;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.services.BetreuungService;
import ch.dvbern.ebegu.services.KindService;
import io.swagger.annotations.Api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

/**
 * REST Resource fuer Kinder
 */
@Path("betreuungen")
@Stateless
@Api
public class BetreuungResource {

	@Inject
	private BetreuungService betreuungService;
	@Inject
	private KindService kindService;
	@Inject
	private JaxBConverter converter;


	@Nullable
	@PUT
	@Path("/{kindId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JaxBetreuung saveBetreuung(
		@Nonnull @NotNull @PathParam("kindId") JaxId kindId,
		@Nonnull @NotNull @Valid JaxBetreuung betreuungJAXP,
		@Context UriInfo uriInfo,
		@Context HttpServletResponse response) throws EbeguException {

		Optional<KindContainer> kind = kindService.findKind(kindId.getId());
		if (kind.isPresent()) {
			Betreuung retrievedBetreuung = new Betreuung();
			if (betreuungJAXP.getId() != null) {
				Optional<Betreuung> optionalBetreuung = betreuungService.findBetreuung(betreuungJAXP.getId());
				retrievedBetreuung = optionalBetreuung.orElse(new Betreuung());
			}

			Betreuung convertedBetreuung = converter.betreuungToEntity(betreuungJAXP, retrievedBetreuung);
			convertedBetreuung.setKind(kind.get());

			Betreuung persistedBetreuung = this.betreuungService.saveBetreuung(convertedBetreuung);
			return converter.betreuungToJAX(persistedBetreuung);
		}
		throw new EbeguEntityNotFoundException("saveBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "KindContainerId invalid: " + kindId.getId());
	}

}
