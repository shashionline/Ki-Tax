package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * Tests fuer die Klasse FinanzielleSituationService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class TestfaelleServiceBeanTest extends AbstractEbeguTest {

	@Inject
	private TestfaelleService testfaelleService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private TraegerschaftService traegerschaftService;

	@Inject
	private Persistence<?> persistence;

	@Test
	@Ignore
	public void saveVerfuegung() {

		// TODO: Hier fehlen noch die Tests für Vergleich der Verfügungszeitabschnitte!
		insertNewEntity(true);
		Gesuch gsw = testfaelleService.createAndSaveTestfaelle("1", true, true);
		Assert.assertNotNull(gsw);

	}

	private Gesuchsperiode insertNewEntity(boolean active) {
		Gesuchsperiode gesuchsperiode = TestDataUtil.createDefaultGesuchsperiode();
		gesuchsperiode.setActive(active);
		gesuchsperiodeService.saveGesuchsperiode(gesuchsperiode);
		return gesuchsperiode;
	}

	private Institution insertInstitution() {
		Institution institution = TestDataUtil.createDefaultInstitution();

		Traegerschaft traegerschaft = TestDataUtil.createDefaultTraegerschaft();
		traegerschaftService.saveTraegerschaft(traegerschaft);
		institution.setTraegerschaft(traegerschaft);

		Mandant mandant = TestDataUtil.createDefaultMandant();
		persistence.persist(mandant);
		institution.setMandant(mandant);

		institutionService.createInstitution(institution);
		return institution;
	}


}

