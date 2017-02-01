package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.MitteilungStatus;
import ch.dvbern.ebegu.enums.MitteilungTeilnehmerTyp;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.services.MitteilungService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;
import java.util.Collection;
import java.util.Optional;

/**
 * Tests fuer die Klasse MitteilungService
 */
@SuppressWarnings("ALL")
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class MitteilungServiceBeanTest extends AbstractEbeguLoginTest {

	@Inject
	private MitteilungService mitteilungService;

	@Inject
	private Persistence<AbstractEntity> persistence;

	private Mandant mandant;
	private Fall fall;
	private Benutzer empfaengerJA;
	private Benutzer empfaengerINST;
	private Benutzer sender;


	@Test
	public void testCreateMitteilung() {
		prepareDependentObjects();
		Mitteilung mitteilung = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		final Mitteilung persistedMitteilung = mitteilungService.sendMitteilung(mitteilung);

		final Optional<Mitteilung> foundMitteilung = mitteilungService.findMitteilung(persistedMitteilung.getId());

		Assert.assertTrue(foundMitteilung.isPresent());
		Assert.assertEquals(mitteilung.getMessage(), foundMitteilung.get().getMessage());
	}

	@Test
	public void testSetMitteilungGelesen() {
		prepareDependentObjects();
		Mitteilung mitteilung = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		final Mitteilung persistedMitteilung = mitteilungService.sendMitteilung(mitteilung);

		final Optional<Mitteilung> foundMitteilung = mitteilungService.findMitteilung(persistedMitteilung.getId());
		Assert.assertTrue(foundMitteilung.isPresent());
		Assert.assertEquals(MitteilungStatus.NEU, foundMitteilung.get().getMitteilungStatus());

		Mitteilung mitteilungNewStatus = mitteilungService.setMitteilungGelesen(foundMitteilung.get().getId());
		Assert.assertNotNull(mitteilungNewStatus);
		Assert.assertEquals(MitteilungStatus.GELESEN, mitteilungNewStatus.getMitteilungStatus());
	}

	@Test
	public void testSetMitteilungErledigt() {
		prepareDependentObjects();
		Mitteilung mitteilung = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		final Mitteilung persistedMitteilung = mitteilungService.sendMitteilung(mitteilung);

		final Optional<Mitteilung> foundMitteilung = mitteilungService.findMitteilung(persistedMitteilung.getId());
		Assert.assertTrue(foundMitteilung.isPresent());
		Assert.assertEquals(MitteilungStatus.NEU, foundMitteilung.get().getMitteilungStatus());
		mitteilungService.setMitteilungGelesen(foundMitteilung.get().getId());

		Mitteilung mitteilungNewStatus = mitteilungService.setMitteilungErledigt(foundMitteilung.get().getId());
		Assert.assertNotNull(mitteilungNewStatus);
		Assert.assertEquals(MitteilungStatus.ERLEDIGT, mitteilungNewStatus.getMitteilungStatus());
	}

	@Test
	public void testGetMitteilungenForCurrentRole() throws LoginException {
		prepareDependentObjects();
		Mitteilung mitteilung1 = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilungService.sendMitteilung(mitteilung1);

		Mitteilung mitteilung2 = TestDataUtil.createMitteilung(fall, empfaengerINST, MitteilungTeilnehmerTyp.INSTITUTION,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilungService.sendMitteilung(mitteilung2);

		final Collection<Mitteilung> mitteilungenForCurrentRolle = mitteilungService.getMitteilungenForCurrentRolle(mitteilung1.getFall());

		//AS SUPERADMIN
		Assert.assertNotNull(mitteilungenForCurrentRolle);
		Assert.assertEquals(1, mitteilungenForCurrentRolle.size());
		Assert.assertEquals(MitteilungTeilnehmerTyp.JUGENDAMT, mitteilungenForCurrentRolle.iterator().next().getEmpfaengerTyp());
	}

	@Test
	public void testGetNewMitteilungenForCurrentRolle() throws LoginException {
		prepareDependentObjects();
		Mitteilung mitteilung1 = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilung1.setMitteilungStatus(MitteilungStatus.NEU);
		mitteilung1.setMessage("Neue Mitteilung");
		persistence.persist(mitteilung1);

		Mitteilung mitteilung2 = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilung2.setMitteilungStatus(MitteilungStatus.GELESEN);
		persistence.persist(mitteilung2);

		final Collection<Mitteilung> newMitteilungenCurrentRolle = mitteilungService.getNewMitteilungenForCurrentRolle(mitteilung1.getFall());

		//AS SUPERADMIN
		Assert.assertNotNull(newMitteilungenCurrentRolle);
		Assert.assertEquals(1, newMitteilungenCurrentRolle.size());
		final Mitteilung foundMitteilung = newMitteilungenCurrentRolle.iterator().next();
		Assert.assertEquals(MitteilungTeilnehmerTyp.JUGENDAMT, foundMitteilung.getEmpfaengerTyp());
		Assert.assertEquals("Neue Mitteilung", foundMitteilung.getMessage());
	}

	@Test
	public void testGetMitteilungenForPosteingang() throws LoginException {
		prepareDependentObjects();
		Mitteilung mitteilung1 = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilungService.sendMitteilung(mitteilung1);

		Mitteilung mitteilung2 = TestDataUtil.createMitteilung(fall, null, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilungService.sendMitteilung(mitteilung2);

		final Collection<Mitteilung> mitteilungenForCurrentRolle = mitteilungService.getMitteilungenForPosteingang();

		//AS SUPERADMIN
		Assert.assertNotNull(mitteilungenForCurrentRolle);
		Assert.assertEquals(1, mitteilungenForCurrentRolle.size());
		Assert.assertEquals(MitteilungTeilnehmerTyp.JUGENDAMT, mitteilungenForCurrentRolle.iterator().next().getEmpfaengerTyp());
	}

	@Test
	public void testSetAllNewMitteilungenOfFallGelesen() {
		prepareDependentObjects();

		Mitteilung entwurf = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		final Mitteilung persistedEntwurf = mitteilungService.saveEntwurf(entwurf);

		Mitteilung mitteilung1 = TestDataUtil.createMitteilung(fall, empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT,
			sender, MitteilungTeilnehmerTyp.GESUCHSTELLER);
		mitteilung1.setMitteilungStatus(MitteilungStatus.NEU);
		final Mitteilung mitFromGSToJA = persistence.persist(mitteilung1);

		Mitteilung mitteilung2 = TestDataUtil.createMitteilung(fall, sender, MitteilungTeilnehmerTyp.GESUCHSTELLER,
			empfaengerJA, MitteilungTeilnehmerTyp.JUGENDAMT);
		mitteilung2.setMitteilungStatus(MitteilungStatus.NEU);
		final Mitteilung mitFromJAToGS = persistence.persist(mitteilung2);

		Assert.assertEquals(MitteilungStatus.ENTWURF, persistedEntwurf.getMitteilungStatus());
		Assert.assertEquals(MitteilungStatus.NEU, mitFromGSToJA.getMitteilungStatus());
		Assert.assertEquals(MitteilungStatus.NEU, mitFromJAToGS.getMitteilungStatus());

		// Set Gelesen as JA
		mitteilungService.setAllNewMitteilungenOfFallGelesen(fall);

		final Optional<Mitteilung> entwurfUpdated1 = mitteilungService.findMitteilung(persistedEntwurf.getId());
		Assert.assertTrue(entwurfUpdated1.isPresent());
		Assert.assertEquals(MitteilungStatus.ENTWURF, entwurfUpdated1.get().getMitteilungStatus());
		final Optional<Mitteilung> mitFromGSToJAUpdated1 = mitteilungService.findMitteilung(mitFromGSToJA.getId());
		Assert.assertTrue(mitFromGSToJAUpdated1.isPresent());
		Assert.assertEquals(MitteilungStatus.GELESEN, mitFromGSToJAUpdated1.get().getMitteilungStatus());
		final Optional<Mitteilung> mitFromJAToGSUpdated1 = mitteilungService.findMitteilung(mitFromJAToGS.getId());
		Assert.assertTrue(mitFromJAToGSUpdated1.isPresent());
		Assert.assertEquals(MitteilungStatus.NEU, mitFromJAToGSUpdated1.get().getMitteilungStatus());

		// Set Gelesen as GS
		loginAsGesuchsteller("gesuchst");
		mitteilungService.setAllNewMitteilungenOfFallGelesen(fall);

		final Optional<Mitteilung> entwurfUpdated2 = mitteilungService.findMitteilung(persistedEntwurf.getId());
		Assert.assertTrue(entwurfUpdated2.isPresent());
		Assert.assertEquals(MitteilungStatus.ENTWURF, entwurfUpdated2.get().getMitteilungStatus());
		final Optional<Mitteilung> mitFromGSToJAUpdated2 = mitteilungService.findMitteilung(mitFromGSToJA.getId());
		Assert.assertTrue(mitFromGSToJAUpdated2.isPresent());
		Assert.assertEquals(MitteilungStatus.GELESEN, mitFromGSToJAUpdated2.get().getMitteilungStatus());
		final Optional<Mitteilung> mitFromJAToGSUpdated2 = mitteilungService.findMitteilung(mitFromJAToGS.getId());
		Assert.assertTrue(mitFromJAToGSUpdated2.isPresent());
		Assert.assertEquals(MitteilungStatus.GELESEN, mitFromJAToGSUpdated2.get().getMitteilungStatus());
	}


	// HELP METHODS

	private void prepareDependentObjects() {
		fall = TestDataUtil.createDefaultFall();
		persistence.persist(fall);
		mandant = TestDataUtil.createDefaultMandant();
		persistence.persist(mandant);
		empfaengerJA = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_JA, "saja", null, null, mandant);
		persistence.persist(empfaengerJA);

		final Traegerschaft traegerschaft = persistence.persist(TestDataUtil.createDefaultTraegerschaft());
		empfaengerINST = TestDataUtil.createBenutzer(UserRole.SACHBEARBEITER_TRAEGERSCHAFT, "insti", traegerschaft, null, mandant);
		persistence.persist(empfaengerINST);

		sender = TestDataUtil.createBenutzer(UserRole.GESUCHSTELLER, "gsst", null, null, mandant);
		persistence.persist(sender);
	}
}