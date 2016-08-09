package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.DokumentGrund_;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Kind
 */
@Stateless
@Local(DokumentGrundService.class)
public class DokumentGrundServiceBean extends AbstractBaseService implements DokumentGrundService {

	@Inject
	private Persistence<DokumentGrund> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	public DokumentGrund saveDokumentGrund(@Nonnull DokumentGrund dokumentGrund) {
		Objects.requireNonNull(dokumentGrund);
		return persistence.merge(dokumentGrund);
	}

	@Override
	@Nonnull
	public Optional<DokumentGrund> findDokumentGrund(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		DokumentGrund a = persistence.find(DokumentGrund.class, key);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	public Collection<DokumentGrund> getAllDokumentGrundByGesuch(@Nonnull Gesuch gesuch) {
		Objects.requireNonNull(gesuch);
		return criteriaQueryHelper.getEntitiesByAttribute(DokumentGrund.class, gesuch, DokumentGrund_.gesuch);
	}

	@Override
	@Nullable
	public DokumentGrund updateDokumentGrund(@Nonnull DokumentGrund dokumentGrund) {
		Objects.requireNonNull(dokumentGrund);

		//Wenn DokumentGrund keine Dokumente mehr hat und nicht gebraucht wird, wird er entfernt
		if (!dokumentGrund.isNeeded() && (dokumentGrund.getDokumente() == null || dokumentGrund.getDokumente().isEmpty())) {
			persistence.remove(dokumentGrund);
			return null;
		}
		return persistence.merge(dokumentGrund);
	}

}