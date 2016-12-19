package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.testfaelle.*;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service fuer erstellen und mutieren von Testfällen
 */
@Stateless
@Local(TestfaelleService.class)
@RolesAllowed(value = {UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN})
public class TestfaelleServiceBean extends AbstractBaseService implements TestfaelleService {


	@Inject
	private GesuchsperiodeService gesuchsperiodeService;
	@Inject
	private InstitutionStammdatenService institutionStammdatenService;
	@Inject
	private GesuchService gesuchService;
	@Inject
	private FallService fallService;
	@Inject
	private BenutzerService benutzerService;
	@Inject
	private FamiliensituationService familiensituationService;
	@Inject
	private GesuchstellerService gesuchstellerService;
	@Inject
	private KindService kindService;
	@Inject
	private BetreuungService betreuungService;
	@Inject
	private ErwerbspensumService erwerbspensumService;
	@Inject
	private FinanzielleSituationService finanzielleSituationService;
	@Inject
	private EinkommensverschlechterungInfoService einkommensverschlechterungInfoService;
	@Inject
	private EinkommensverschlechterungService einkommensverschlechterungService;
	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private VerfuegungService verfuegungService;

	private static final Logger LOG = LoggerFactory.getLogger(TestfaelleServiceBean.class);

	@Override
	@Nonnull
	public StringBuilder createAndSaveTestfaelle(String fallid,
												 Integer iterationCount,
												 boolean betreuungenBestaetigt,
												 boolean verfuegen) {
		return this.createAndSaveTestfaelle(fallid, iterationCount, betreuungenBestaetigt, verfuegen, null);
	}

	@Nonnull
	@SuppressWarnings(value = {"PMD.NcssMethodCount", "PMD.AvoidDuplicateLiterals"})
	public StringBuilder createAndSaveTestfaelle(String fallid,
												 Integer iterationCount,
												 boolean betreuungenBestaetigt,
												 boolean verfuegen, Benutzer besitzer) {

		iterationCount = (iterationCount == null || iterationCount == 0) ? 1 : iterationCount;

		Gesuchsperiode gesuchsperiode = getGesuchsperiode();
		List<InstitutionStammdaten> institutionStammdatenList = getInstitutionStammdatens();

		StringBuilder responseString = new StringBuilder("");
		for (int i = 0; i < iterationCount; i++) {

			if (WaeltiDagmar.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Dagmar Waelti erstellt, Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (FeutzIvonne.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall02_FeutzYvonne(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Yvonne Feutz erstellt, Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (PerreiraMarcia.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Marcia Perreira erstellt, Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (WaltherLaura.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall04_WaltherLaura(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Laura Walther erstellt, Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (LuethiMeret.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall05_LuethiMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Meret Luethi erstellt, Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (BeckerNora.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall06_BeckerNora(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Nora Becker erstellt, Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (MeierMeret.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall07_MeierMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Meier Meret erstellt, Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (UmzugAusInAusBern.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall08_UmzugAusInAusBern(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Umzug Aus-In-Aus Bern Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (Abwesenheit.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall09_Abwesenheit(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Abwesenheit Fallnummer: ").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if ("all".equals(fallid)) {
				createAndSaveGesuch(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall02_FeutzYvonne(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall04_WaltherLaura(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall05_LuethiMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall06_BeckerNora(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall07_MeierMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall08_UmzugAusInAusBern(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall09_Abwesenheit(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Testfaelle 1-9 erstellt");
			} else {
				responseString.append("Usage: /Nummer des Testfalls an die URL anhaengen. Bisher umgesetzt: 1-9. '/all' erstellt alle Testfaelle");
			}

		}
		return responseString;
	}


	public StringBuilder createAndSaveAsOnlineGesuch(@Nonnull String fallid,
													 boolean betreuungenBestaetigt,
													 boolean verfuegen, @Nonnull String username) {

		removeGesucheOfGS(username);
		Benutzer benutzer = benutzerService.findBenutzer(username).orElse(benutzerService.getCurrentBenutzer().orElse(null));
		return this.createAndSaveTestfaelle(fallid, 1, true, false, benutzer);

	}


	@Override
	@Nullable
	public Gesuch createAndSaveTestfaelle(String fallid,
										  boolean betreuungenBestaetigt,
										  boolean verfuegen) {

		Gesuchsperiode gesuchsperiode = getGesuchsperiode();
		List<InstitutionStammdaten> institutionStammdatenList = getInstitutionStammdatens();

		if (WaeltiDagmar.equals(fallid)) {
			return createAndSaveGesuch(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (FeutzIvonne.equals(fallid)) {
			return createAndSaveGesuch(new Testfall02_FeutzYvonne(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (PerreiraMarcia.equals(fallid)) {
			return createAndSaveGesuch(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (WaltherLaura.equals(fallid)) {
			return createAndSaveGesuch(new Testfall04_WaltherLaura(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (LuethiMeret.equals(fallid)) {
			return createAndSaveGesuch(new Testfall05_LuethiMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (BeckerNora.equals(fallid)) {
			return createAndSaveGesuch(new Testfall06_BeckerNora(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (MeierMeret.equals(fallid)) {
			return createAndSaveGesuch(new Testfall07_MeierMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (UmzugAusInAusBern.equals(fallid)) {
			return createAndSaveGesuch(new Testfall08_UmzugAusInAusBern(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (Abwesenheit.equals(fallid)) {
			return createAndSaveGesuch(new Testfall09_Abwesenheit(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}

		return null;
	}

	@Override
	public void removeGesucheOfGS(String username) {
		Benutzer benutzer = benutzerService.findBenutzer(username).orElse(benutzerService.getCurrentBenutzer().orElse(null));
		Optional<Fall> existingFall = fallService.findFallByBesitzer(benutzer);
		if (existingFall.isPresent()) {
			//unschoen: eigentlich nur das gesuch fuer das jahr loeschen fuer welches der testfall erzeugt wird
			List<String> allGesucheForBenutzer = gesuchService.getAllGesuchIDsForFall(existingFall.get().getId());
			allGesucheForBenutzer
				.forEach(gesuchId -> gesuchService.findGesuch(gesuchId)
					.ifPresent((gesuch) -> {
						LOG.info("Removing Gesuch for user " + (benutzer != null ? benutzer.getUsername() : "-") + " with id " + gesuch.getId());
						gesuchService.removeGesuch(gesuch);
					}));
		}

	}

	@Override
	public Gesuch mutierenHeirat(@Nonnull Long fallNummer, @Nonnull String gesuchsperiodeId, @Nonnull LocalDate eingangsdatum, LocalDate aenderungPer, boolean verfuegen) {
		Validate.notNull(eingangsdatum);
		Validate.notNull(gesuchsperiodeId);
		Validate.notNull(fallNummer);
		Validate.notNull(aenderungPer);

		Familiensituation newFamsit = new Familiensituation();
		newFamsit.setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		newFamsit.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ZU_ZWEIT);
		newFamsit.setGemeinsameSteuererklaerung(true);
		newFamsit.setAenderungPer(aenderungPer);

		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(fallNummer, gesuchsperiodeId, eingangsdatum);
		if (gesuchOptional.isPresent()) {
			final Gesuch mutation = gesuchOptional.get();
			final FamiliensituationContainer familiensituationContainer = mutation.getFamiliensituationContainer();
			familiensituationContainer.setFamiliensituationErstgesuch(familiensituationContainer.getFamiliensituationJA());
			familiensituationContainer.setFamiliensituationJA(newFamsit);

			familiensituationService.saveFamiliensituation(mutation, familiensituationContainer);
			final GesuchstellerContainer gesuchsteller2 = gesuchstellerService
				.saveGesuchsteller(createGesuchstellerHeirat(mutation.getGesuchsteller1()), mutation, 2, false);

			mutation.setGesuchsteller2(gesuchsteller2);
			gesuchService.createGesuch(mutation);
			gesuchVerfuegenUndSpeichern(verfuegen, mutation, true);
			return mutation;
		}

		return gesuchOptional.orElse(null);
	}

	@Override
	public Gesuch mutierenScheidung(@Nonnull Long fallNummer, @Nonnull String gesuchsperiodeId, @Nonnull LocalDate eingangsdatum, LocalDate aenderungPer, boolean verfuegen) {

		Validate.notNull(eingangsdatum);
		Validate.notNull(gesuchsperiodeId);
		Validate.notNull(fallNummer);
		Validate.notNull(aenderungPer);

		Familiensituation newFamsit = new Familiensituation();
		newFamsit.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		newFamsit.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		newFamsit.setAenderungPer(aenderungPer);

		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(fallNummer, gesuchsperiodeId, eingangsdatum);
		if (gesuchOptional.isPresent()) {
			final Gesuch mutation = gesuchOptional.get();
			final FamiliensituationContainer familiensituationContainer = mutation.getFamiliensituationContainer();
			familiensituationContainer.setFamiliensituationErstgesuch(familiensituationContainer.getFamiliensituationJA());
			familiensituationContainer.setFamiliensituationJA(newFamsit);
			familiensituationService.saveFamiliensituation(mutation, familiensituationContainer);
			gesuchService.createGesuch(mutation);
			gesuchVerfuegenUndSpeichern(verfuegen, mutation, true);
			return mutation;
		}

		return gesuchOptional.orElse(null);
	}

	private Gesuchsperiode getGesuchsperiode() {
		Collection<Gesuchsperiode> allActiveGesuchsperioden = gesuchsperiodeService.getAllActiveGesuchsperioden();
		return allActiveGesuchsperioden.iterator().next();
	}

	private List<InstitutionStammdaten> getInstitutionStammdatens() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		Optional<InstitutionStammdaten> optionalAaregg = institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.ID_INSTITUTION_STAMMDATEN_WEISSENSTEIN_KITA);
		Optional<InstitutionStammdaten> optionalBruennen = institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.ID_INSTITUTION_STAMMDATEN_BRUENNEN_KITA);
		Optional<InstitutionStammdaten> optionalTagiAaregg = institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.ID_INSTITUTION_STAMMDATEN_WEISSENSTEIN_TAGI);

		optionalAaregg.ifPresent(institutionStammdatenList::add);
		optionalBruennen.ifPresent(institutionStammdatenList::add);
		optionalTagiAaregg.ifPresent(institutionStammdatenList::add);
		return institutionStammdatenList;
	}

	/**
	 * Diese Methode ist etwas lang und haesslich aber das ist weil wir versuchen, den ganzen Prozess zu simulieren. D.h. wir speichern
	 * alle Objekte hintereinander, um die entsprechenden Services auszufuehren, damit die interne Logik auch durchgefuehrt wird.
	 * Nachteil ist, dass man vor allem die WizardSteps vorbereiten muss, damit der Prozess so laeuft wie auf dem web browser.
	 * <p>
	 * Am Ende der Methode und zur Sicherheit, updaten wir das Gesuch ein letztes Mal, um uns zu vergewissern, dass alle Daten gespeichert wurden.
	 * <p>
	 * Die Methode geht davon aus, dass die Daten nur eingetragen wurden und noch keine Betreuung bzw. Verfuegung bearbeitet ist.
	 * Aus diesem Grund, bleibt das Gesuch mit Status IN_BEARBEITUNG_JA
	 *
	 * @param fromTestfall testfall
	 * @param besitzer     wenn der besitzer gesetzt ist wird der fall diesem besitzer zugeordnet
	 */
	private Gesuch createAndSaveGesuch(AbstractTestfall fromTestfall, boolean verfuegen, @Nullable Benutzer besitzer) {
		final Optional<List<Gesuch>> gesuchByGSName = gesuchService.findGesuchByGSName(fromTestfall.getNachname(), fromTestfall.getVorname());
		if (gesuchByGSName.isPresent()) {
			final List<Gesuch> gesuches = gesuchByGSName.get();
			if (!gesuches.isEmpty()) {
				fromTestfall.setFall(gesuches.iterator().next().getFall());
			}
		}

		final Optional<Benutzer> currentBenutzer = benutzerService.getCurrentBenutzer();
		Optional<Fall> fallByBesitzer = fallService.findFallByBesitzer(besitzer); //fall kann schon existieren
		Fall fall;
		if (!fallByBesitzer.isPresent()) {
			if (currentBenutzer.isPresent()) {
				fall = fromTestfall.createFall(currentBenutzer.get());
			} else {
				fall = fromTestfall.createFall();
			}
		} else {
			fall = fallByBesitzer.get();
			fall.setNextNumberKind(1); //reset
		}
		if (besitzer != null) {
			fall.setBesitzer(besitzer);
		}
		final Fall persistedFall = fallService.saveFall(fall);
		fromTestfall.setFall(persistedFall); // dies wird gebraucht, weil fallService.saveFall ein merge macht.

		fromTestfall.createGesuch(LocalDate.of(2016, Month.FEBRUARY, 15));
		gesuchService.createGesuch(fromTestfall.getGesuch());
		Gesuch gesuch = fromTestfall.fillInGesuch();

		if (besitzer != null) {
			gesuch.setStatus(AntragStatus.IN_BEARBEITUNG_GS);
			gesuch.setEingangsart(Eingangsart.ONLINE);
		} else {
			gesuch.setEingangsart(Eingangsart.PAPIER);
		}

		gesuchVerfuegenUndSpeichern(verfuegen, gesuch, false);

		return gesuch;

	}

	private void gesuchVerfuegenUndSpeichern(boolean verfuegen, Gesuch gesuch, boolean mutation) {
		final List<WizardStep> wizardStepsFromGesuch = wizardStepService.findWizardStepsFromGesuch(gesuch.getId());

		if (!mutation) {
			saveFamiliensituation(gesuch, wizardStepsFromGesuch);
			saveGesuchsteller(gesuch, wizardStepsFromGesuch);
			saveKinder(gesuch, wizardStepsFromGesuch);
			saveBetreuungen(gesuch, wizardStepsFromGesuch);
			saveErwerbspensen(gesuch, wizardStepsFromGesuch);
			saveFinanzielleSituation(gesuch, wizardStepsFromGesuch);
			saveEinkommensverschlechterung(gesuch, wizardStepsFromGesuch);

			gesuchService.updateGesuch(gesuch, false); // just save all other objects before updating dokumente and verfuegungen
			saveDokumente(wizardStepsFromGesuch);
			saveVerfuegungen(gesuch, wizardStepsFromGesuch);
		}

		if (verfuegen) {
			verfuegungService.calculateVerfuegung(gesuch);
			gesuch.getKindContainers().stream().forEach(kindContainer -> {
				kindContainer.getBetreuungen().stream().forEach(betreuung -> {
					verfuegungService.persistVerfuegung(betreuung.getVerfuegung(), betreuung.getId(), Betreuungsstatus.VERFUEGT);
				});
			});
		}

		wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.VERFUEGEN);
	}

	private void saveVerfuegungen(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		if (!gesuch.getStatus().isAnyStatusOfVerfuegt()) {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.VERFUEGEN, WizardStepStatus.WARTEN);
		} else {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.VERFUEGEN, WizardStepStatus.OK);
		}
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.VERFUEGEN);
	}

	private void saveDokumente(List<WizardStep> wizardStepsFromGesuch) {
		setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.DOKUMENTE, WizardStepStatus.IN_BEARBEITUNG);
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.DOKUMENTE);
	}

	private void saveEinkommensverschlechterung(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		if (gesuch.getEinkommensverschlechterungInfoContainer() != null) {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.EINKOMMENSVERSCHLECHTERUNG, WizardStepStatus.IN_BEARBEITUNG);
			einkommensverschlechterungInfoService.createEinkommensverschlechterungInfo(gesuch.getEinkommensverschlechterungInfoContainer());
		}
		if (gesuch.getGesuchsteller1() != null && gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer() != null) {
			einkommensverschlechterungService.saveEinkommensverschlechterungContainer(gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer());
		}
		if (gesuch.getGesuchsteller2() != null && gesuch.getGesuchsteller2().getEinkommensverschlechterungContainer() != null) {
			einkommensverschlechterungService.saveEinkommensverschlechterungContainer(gesuch.getGesuchsteller2().getEinkommensverschlechterungContainer());
		}
		setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.EINKOMMENSVERSCHLECHTERUNG, WizardStepStatus.OK);
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.EINKOMMENSVERSCHLECHTERUNG);
	}

	private void saveFinanzielleSituation(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		if (gesuch.getGesuchsteller1() != null && gesuch.getGesuchsteller1().getFinanzielleSituationContainer() != null) {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.FINANZIELLE_SITUATION, WizardStepStatus.IN_BEARBEITUNG);
			finanzielleSituationService.saveFinanzielleSituation(gesuch.getGesuchsteller1().getFinanzielleSituationContainer(), gesuch.getId());
		}
		if (gesuch.getGesuchsteller2() != null && gesuch.getGesuchsteller2().getFinanzielleSituationContainer() != null) {
			finanzielleSituationService.saveFinanzielleSituation(gesuch.getGesuchsteller2().getFinanzielleSituationContainer(), gesuch.getId());
		}
		setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.FINANZIELLE_SITUATION, WizardStepStatus.OK);
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.FINANZIELLE_SITUATION);
	}

	private void saveErwerbspensen(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		if (gesuch.getGesuchsteller1() != null) {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.ERWERBSPENSUM, WizardStepStatus.IN_BEARBEITUNG);
			gesuch.getGesuchsteller1().getErwerbspensenContainers()
				.forEach(erwerbspensumContainer -> erwerbspensumService.saveErwerbspensum(erwerbspensumContainer, gesuch));
		}
		if (gesuch.getGesuchsteller2() != null) {
			gesuch.getGesuchsteller2().getErwerbspensenContainers()
				.forEach(erwerbspensumContainer -> erwerbspensumService.saveErwerbspensum(erwerbspensumContainer, gesuch));
		}
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.ERWERBSPENSUM);
	}

	private void saveBetreuungen(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.BETREUUNG, WizardStepStatus.IN_BEARBEITUNG);
		gesuch.getKindContainers().forEach(kindContainer
			-> kindContainer.getBetreuungen().forEach(betreuung -> betreuungService.saveBetreuung(betreuung, false)));
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.BETREUUNG);
	}

	private void saveKinder(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.KINDER, WizardStepStatus.IN_BEARBEITUNG);
		gesuch.getKindContainers().forEach(kindContainer -> kindService.saveKind(kindContainer));
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.KINDER);
	}

	private void saveGesuchsteller(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		if (gesuch.getGesuchsteller1() != null) {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.GESUCHSTELLER, WizardStepStatus.IN_BEARBEITUNG);
			gesuchstellerService.saveGesuchsteller(gesuch.getGesuchsteller1(), gesuch, 1, false);
		}
		if (gesuch.getGesuchsteller2() != null) {
			gesuchstellerService.saveGesuchsteller(gesuch.getGesuchsteller2(), gesuch, 2, false);
		}
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.GESUCHSTELLER);
		// Umzug wird by default OK und verfuegbar, da es nicht notwendig ist, einen Umzug einzutragen
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.UMZUG);
		setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.UMZUG, WizardStepStatus.OK);
	}

	private void saveFamiliensituation(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		if (gesuch.extractFamiliensituation() != null) {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.FAMILIENSITUATION, WizardStepStatus.IN_BEARBEITUNG);
			familiensituationService.saveFamiliensituation(gesuch, gesuch.getFamiliensituationContainer());
			setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.FAMILIENSITUATION);
		}
	}

	private void setWizardStepInStatus(List<WizardStep> wizardSteps, WizardStepName stepName, WizardStepStatus status) {
		final WizardStep wizardStep = getWizardStepByName(wizardSteps, stepName);
		if (wizardStep != null) {
			wizardStep.setWizardStepStatus(status);
			wizardStepService.saveWizardStep(wizardStep);
		}
	}

	private void setWizardStepVerfuegbar(List<WizardStep> wizardSteps, WizardStepName stepName) {
		final WizardStep wizardStep = getWizardStepByName(wizardSteps, stepName);
		if (wizardStep != null) {
			wizardStep.setVerfuegbar(true);
			wizardStepService.saveWizardStep(wizardStep);
		}
	}

	private WizardStep getWizardStepByName(List<WizardStep> wizardSteps, WizardStepName stepName) {
		for (WizardStep wizardStep : wizardSteps) {
			if (stepName.equals(wizardStep.getWizardStepName())) {
				return wizardStep;
			}
		}
		return null;
	}

	private GesuchstellerContainer createGesuchstellerHeirat(GesuchstellerContainer gesuchsteller1) {
		GesuchstellerContainer gesuchsteller2 = new GesuchstellerContainer();
		Gesuchsteller gesuchsteller = new Gesuchsteller();
		gesuchsteller.setGeburtsdatum(LocalDate.of(1984, 12, 12));
		gesuchsteller.setVorname("Tim");
		gesuchsteller.setNachname(gesuchsteller1.extractNachname());
		gesuchsteller.setGeschlecht(Geschlecht.MAENNLICH);
		gesuchsteller.setMail("tim.tester@example.com");
		gesuchsteller.setMobile("076 309 30 58");
		gesuchsteller.setTelefon("031 378 24 24");
		gesuchsteller.setZpvNumber("0761234567897");

		gesuchsteller2.setGesuchstellerJA(gesuchsteller);
		gesuchsteller2.addAdresse(createGesuchstellerAdresseHeirat(gesuchsteller2));

		final ErwerbspensumContainer erwerbspensumContainer = createErwerbspensumContainer();
		erwerbspensumContainer.setGesuchsteller(gesuchsteller2);
		gesuchsteller2.getErwerbspensenContainers().add(erwerbspensumContainer);

		return gesuchsteller2;
	}

	private GesuchstellerAdresseContainer createGesuchstellerAdresseHeirat(GesuchstellerContainer gsCont) {
		GesuchstellerAdresseContainer gsAdresseContainer = new GesuchstellerAdresseContainer();

		GesuchstellerAdresse gesuchstellerAdresse = new GesuchstellerAdresse();
		gesuchstellerAdresse.setStrasse("Nussbaumstrasse");
		gesuchstellerAdresse.setHausnummer("21");
		gesuchstellerAdresse.setZusatzzeile("c/o Uwe Untermieter");
		gesuchstellerAdresse.setPlz("3014");
		gesuchstellerAdresse.setOrt("Bern");
		gesuchstellerAdresse.setGueltigkeit(new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME));
		gesuchstellerAdresse.setAdresseTyp(AdresseTyp.WOHNADRESSE);

		gsAdresseContainer.setGesuchstellerContainer(gsCont);
		gsAdresseContainer.setGesuchstellerAdresseJA(gesuchstellerAdresse);

		return gsAdresseContainer;
	}

	private ErwerbspensumContainer createErwerbspensumContainer() {
		ErwerbspensumContainer epCont = new ErwerbspensumContainer();
		epCont.setErwerbspensumGS(createErwerbspensumData());
		Erwerbspensum epKorrigiertJA = createErwerbspensumData();
		epKorrigiertJA.setTaetigkeit(Taetigkeit.ANGESTELLT);
		epCont.setErwerbspensumJA(epKorrigiertJA);
		return epCont;
	}

	private Erwerbspensum createErwerbspensumData() {
		Erwerbspensum ep = new Erwerbspensum();
		ep.setTaetigkeit(Taetigkeit.ANGESTELLT);
		ep.setPensum(80);
		ep.setZuschlagZuErwerbspensum(true);
		ep.setZuschlagsgrund(Zuschlagsgrund.LANGER_ARBWEITSWEG);
		ep.setZuschlagsprozent(10);
		ep.setGueltigkeit(new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME));
		return ep;
	}
}