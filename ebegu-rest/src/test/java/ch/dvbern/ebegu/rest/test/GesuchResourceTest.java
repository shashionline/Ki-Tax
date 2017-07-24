package ch.dvbern.ebegu.rest.test;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxKindContainer;
import ch.dvbern.ebegu.api.resource.GesuchResource;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.tets.util.JBossLoginContextFactory;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Iterator;

/**
 * Testet GesuchResource
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class GesuchResourceTest extends AbstractEbeguRestLoginTest {

	@Inject
	private GesuchResource gesuchResource;
	@Inject
	private InstitutionService institutionService;
	@Inject
	private Persistence<Gesuch> persistence;
	@Inject
	private JaxBConverter converter;


	private static final Logger LOG = LoggerFactory.getLogger(GesuchResourceTest.class);

	/**
	 * fuer diesen service logen wir uns immer als jemand anderes ein
	 */
	@Test
	public void testFindGesuchForInstitution() throws EbeguException {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		changeStatusToWarten(gesuch.getKindContainers().iterator().next());
		persistUser(UserRole.SACHBEARBEITER_INSTITUTION, "sainst",
			gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution(),
			null, gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution().getMandant());
		final JaxGesuch gesuchForInstitution = gesuchResource.findGesuchForInstitution(converter.toJaxId(gesuch));

		Assert.assertNull(gesuchForInstitution.getEinkommensverschlechterungInfoContainer());

		Assert.assertNotNull(gesuchForInstitution.getGesuchsteller1());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getEinkommensverschlechterungContainer());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getErwerbspensenContainers());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getFinanzielleSituationContainer());

		Assert.assertNull(gesuchForInstitution.getGesuchsteller2()); //GS2 ist von Anfang an nicht gesetzt

		Assert.assertNotNull(gesuchForInstitution.getKindContainers());
		Assert.assertEquals(1, gesuchForInstitution.getKindContainers().size());

		final Iterator<JaxKindContainer> iterator = gesuchForInstitution.getKindContainers().iterator();
		final JaxKindContainer kind = iterator.next();
		Assert.assertNotNull(kind);
		Assert.assertNotNull(kind.getBetreuungen());
		Assert.assertEquals(1, kind.getBetreuungen().size());
	}

	@Test
	public void testFindGesuchForTraegerschaft() throws EbeguException {
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		changeStatusToWarten(gesuch.getKindContainers().iterator().next());

		persistUser(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, "satraeg",  null,
			gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution().getTraegerschaft(),
			gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getInstitutionStammdaten().getInstitution().getMandant());

		final JaxGesuch gesuchForInstitution = gesuchResource.findGesuchForInstitution(converter.toJaxId(gesuch));

		Assert.assertNull(gesuchForInstitution.getEinkommensverschlechterungInfoContainer());

		Assert.assertNotNull(gesuchForInstitution.getGesuchsteller1());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getEinkommensverschlechterungContainer());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getErwerbspensenContainers());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getFinanzielleSituationContainer());

		Assert.assertNull(gesuchForInstitution.getGesuchsteller2()); //GS2 ist von Anfang an nicht gesetzt

		Assert.assertNotNull(gesuchForInstitution.getKindContainers());
		Assert.assertEquals(1, gesuchForInstitution.getKindContainers().size());

		final Iterator<JaxKindContainer> iterator = gesuchForInstitution.getKindContainers().iterator();
		final JaxKindContainer kind = iterator.next();
		Assert.assertNotNull(kind);
		Assert.assertNotNull(kind.getBetreuungen());
		Assert.assertEquals(1, kind.getBetreuungen().size());
	}

	@Test
	public void testFindGesuchForOtherRole() throws EbeguException {
		persistUser(UserRole.GESUCHSTELLER, "gesuchst", null, null, null);
		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));


		final JaxGesuch gesuchForInstitution = gesuchResource.findGesuchForInstitution(converter.toJaxId(gesuch));

		Assert.assertNull(gesuchForInstitution.getEinkommensverschlechterungInfoContainer());

		Assert.assertNotNull(gesuchForInstitution.getGesuchsteller1());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getEinkommensverschlechterungContainer());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getErwerbspensenContainers());
		Assert.assertNull(gesuchForInstitution.getGesuchsteller1().getFinanzielleSituationContainer());

		Assert.assertNull(gesuchForInstitution.getGesuchsteller2()); //GS2 ist von Anfang an nicht gesetzt

		Assert.assertNotNull(gesuchForInstitution.getKindContainers());
		Assert.assertEquals(1, gesuchForInstitution.getKindContainers().size());
	}

	@Test
	public void testUpdateStatus() throws EbeguException {
		persistUser(UserRole.SACHBEARBEITER_JA, "saja", null, null, null);

		final Gesuch gesuch = TestDataUtil.createAndPersistWaeltiDagmarGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));

		Response response = gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.ERSTE_MAHNUNG);
		final JaxGesuch persistedGesuch = gesuchResource.findGesuch(new JaxId(gesuch.getId()));

		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals(AntragStatusDTO.ERSTE_MAHNUNG, persistedGesuch.getStatus());
	}

	@Test
	public void testAntragMutieren() throws EbeguException {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		gesuch.setGueltig(true);
		gesuch.setTimestampVerfuegt(LocalDateTime.now());
		gesuch = persistence.merge(gesuch);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.VERFUEGT).getEntity();

		final Response response = gesuchResource.antragMutieren(new JaxId(gesuch.getId()), LocalDate.now().toString(), null, null);

		Assert.assertNotNull(response);
		final Object entity = response.getEntity();
		Assert.assertNotNull(entity);
	}

	@Test
	public void testGesuchBySTVFreigeben_NotExistingGesuch() {
		try {
			gesuchResource.gesuchBySTVFreigeben(new JaxId("dfafdasf"), null, null);
			Assert.fail("Das Gesuch existiert nicht. Muss eine Exception werfen");
		} catch (EbeguEntityNotFoundException e) {
			//nop
		}
	}

	@Test
	public void testGesuchBySTVFreigeben() throws EbeguException {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.IN_BEARBEITUNG_STV);

		final Response response = gesuchResource.gesuchBySTVFreigeben(new JaxId(gesuch.getId()), null, null);

		final Object entity = response.getEntity();
		Assert.assertTrue(entity instanceof JaxGesuch);
		final JaxGesuch jaxGesuch = (JaxGesuch) entity;
		Assert.assertEquals(AntragStatusDTO.GEPRUEFT_STV, jaxGesuch.getStatus());
		Assert.assertTrue(jaxGesuch.isGeprueftSTV());
	}

	@Test
	public void testGesuchBySTVFreigeben_NotInBearbeitungSTV() {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		try {
			gesuchResource.sendGesuchToSTV(new JaxId(gesuch.getId()), null, null, null);
			Assert.fail("Das Gesuch ist nicht In Bearbeitung STV. Muss eine Exception werfen");
		} catch (EbeguRuntimeException e) {
			//nop
		}
	}

	@Test
	public void testSendGesuchToSTV_NotExistingGesuch() {
		try {
			gesuchResource.sendGesuchToSTV(new JaxId("dfafdasf"), null, null, null);
			Assert.fail("Das Gesuch existiert nicht. Muss eine Exception werfen");
		} catch (EbeguEntityNotFoundException e) {
			//nop
		}
	}

	@Test
	public void testSendGesuchToSTV_NotVerfuegt() {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		try {
			gesuchResource.sendGesuchToSTV(new JaxId(gesuch.getId()), null, null, null);
			Assert.fail("Das Gesuch ist nicht verfuegt. Muss eine Exception werfen");
		} catch (EbeguRuntimeException e) {
			//nop
		}
	}

	@Test
	public void testSendGesuchToSTV_NullBemerkung() throws EbeguException {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.VERFUEGT);

		final Response response = gesuchResource.sendGesuchToSTV(new JaxId(gesuch.getId()), null, null, null);

		final Object entity = response.getEntity();
		Assert.assertTrue(entity instanceof JaxGesuch);
		final JaxGesuch jaxGesuch = (JaxGesuch) entity;
		Assert.assertNull(jaxGesuch.getBemerkungenSTV());
		Assert.assertEquals(AntragStatusDTO.PRUEFUNG_STV, jaxGesuch.getStatus());
	}

	@Test
	public void testSendGesuchToSTV() throws EbeguException {
		Gesuch gesuch = TestDataUtil.createAndPersistGesuch(persistence);
		gesuchResource.updateStatus(new JaxId(gesuch.getId()), AntragStatusDTO.VERFUEGT);

		final Response response = gesuchResource.sendGesuchToSTV(new JaxId(gesuch.getId()), "bemerkSTV", null, null);

		final Object entity = response.getEntity();
		Assert.assertTrue(entity instanceof JaxGesuch);
		final JaxGesuch jaxGesuch = (JaxGesuch) entity;
		Assert.assertEquals("bemerkSTV", jaxGesuch.getBemerkungenSTV());
		Assert.assertEquals(AntragStatusDTO.PRUEFUNG_STV, jaxGesuch.getStatus());
	}



	// HELP METHODS

	private Benutzer persistUser(final UserRole role, final String username,  final Institution institution, final Traegerschaft traegerschaft, final Mandant mandant) {
		Mandant mandantToStore = mandant;
		if (mandantToStore == null) {
			mandantToStore = TestDataUtil.createDefaultMandant();
			persistence.persist(mandantToStore);
		}
		Benutzer benutzer = TestDataUtil.createBenutzer(role, username, traegerschaft, institution, mandantToStore);
		persistence.persist(benutzer);
		try {
			JBossLoginContextFactory.createLoginContext(username, username).login();
		} catch (LoginException e) {
			LOG.error("could not log in as user " + username, e);
			throw new RuntimeException("could not log in");
		}
		return benutzer;
	}

	private void changeStatusToWarten(KindContainer kindContainer) {
		for (Betreuung betreuung : kindContainer.getBetreuungen()) {
			betreuung.setBetreuungsstatus(Betreuungsstatus.WARTEN);
			persistence.merge(betreuung);
		}
	}
}
