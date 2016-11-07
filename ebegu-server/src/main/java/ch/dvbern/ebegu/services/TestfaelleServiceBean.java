package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import ch.dvbern.ebegu.testfaelle.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
 * Service fuer FinanzielleSituation
 */
@Stateless
@Local(TestfaelleService.class)
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

	@Nonnull
	public StringBuilder createAndSaveTestfaelle(String fallid,
												 Integer iterationCount,
												 boolean betreuungenBestaetigt,
												 boolean verfuegen) {

		iterationCount = iterationCount == null || iterationCount == 0 ? 1 : iterationCount;

		Gesuchsperiode gesuchsperiode = getGesuchsperiode();
		List<InstitutionStammdaten> institutionStammdatenList = getInstitutionStammdatens();

		StringBuilder responseString = new StringBuilder("");
		for (int i = 0; i < iterationCount; i++) {

			if ("1".equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
				responseString.append("Fall Dagmar Waelti erstellt, Fallnummer ").append(gesuch.getFall().getFallNummer());
			} else if ("2".equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall02_FeutzYvonne(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
				responseString.append("Fall Yvonne Feutz erstellt, Fallnummer ").append(gesuch.getFall().getFallNummer());
			} else if ("3".equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
				responseString.append("Fall Marcia Perreira erstellt, Fallnummer ").append(gesuch.getFall().getFallNummer());
			} else if ("4".equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall04_WaltherLaura(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
				responseString.append("Fall Laura Walther erstellt, Fallnummer ").append(gesuch.getFall().getFallNummer());
			} else if ("5".equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall05_LuethiMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
				responseString.append("Fall Meret Luethi erstellt, Fallnummer ").append(gesuch.getFall().getFallNummer());
			} else if ("6".equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall06_BeckerNora(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
				responseString.append("Fall Nora Becker erstellt, Fallnummer ").append(gesuch.getFall().getFallNummer());
			} else if ("all".equals(fallid)) {
				createAndSaveGesuch(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
				createAndSaveGesuch(new Testfall02_FeutzYvonne(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
				createAndSaveGesuch(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
				createAndSaveGesuch(new Testfall04_WaltherLaura(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
				createAndSaveGesuch(new Testfall05_LuethiMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
				createAndSaveGesuch(new Testfall06_BeckerNora(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
				responseString.append("Testfaelle 1-6 erstellt");
			} else {
				responseString.append("Usage: /Nummer des Testfalls an die URL anhaengen. Bisher umgesetzt: 1-6. '/all' erstellt alle Testfaelle");
			}

		}
		return responseString;
	}


	@Nullable
	public Gesuch createAndSaveTestfaelle(String fallid,
										  boolean betreuungenBestaetigt,
										  boolean verfuegen) {


		Gesuchsperiode gesuchsperiode = getGesuchsperiode();
		List<InstitutionStammdaten> institutionStammdatenList = getInstitutionStammdatens();

		if ("1".equals(fallid)) {
			return createAndSaveGesuch(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
		} else if ("2".equals(fallid)) {
			return createAndSaveGesuch(new Testfall02_FeutzYvonne(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
		} else if ("3".equals(fallid)) {
			return createAndSaveGesuch(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
		} else if ("4".equals(fallid)) {
			return createAndSaveGesuch(new Testfall04_WaltherLaura(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
		} else if ("5".equals(fallid)) {
			return createAndSaveGesuch(new Testfall05_LuethiMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
		} else if ("6".equals(fallid)) {
			return createAndSaveGesuch(new Testfall06_BeckerNora(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen);
		}

		return null;
	}

	private Gesuchsperiode getGesuchsperiode() {
		Collection<Gesuchsperiode> allActiveGesuchsperioden = gesuchsperiodeService.getAllActiveGesuchsperioden();
		return allActiveGesuchsperioden.iterator().next();
	}

	private List<InstitutionStammdaten> getInstitutionStammdatens() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		Optional<InstitutionStammdaten> optionalAaregg = institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.ID_INSTITUTION_AAREGG);
		Optional<InstitutionStammdaten> optionalBruennen = institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.ID_INSTITUTION_BRUENNEN);
		Optional<InstitutionStammdaten> optionalTagiAaregg = institutionStammdatenService.findInstitutionStammdaten("11111111-1111-1111-1111-111111111174");

		if (optionalAaregg.isPresent()) {
			institutionStammdatenList.add(optionalAaregg.get());
		}
		if (optionalBruennen.isPresent()) {
			institutionStammdatenList.add(optionalBruennen.get());
		}
		if (optionalTagiAaregg.isPresent()) {
			institutionStammdatenList.add(optionalTagiAaregg.get());
		}
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
	 */
	private Gesuch createAndSaveGesuch(AbstractTestfall fromTestfall, boolean verfuegen) {
		final Optional<List<Gesuch>> gesuchByGSName = gesuchService.findGesuchByGSName(fromTestfall.getNachname(), fromTestfall.getVorname());
		if (gesuchByGSName.isPresent()) {
			final List<Gesuch> gesuches = gesuchByGSName.get();
			if (!gesuches.isEmpty()) {
				fromTestfall.setFall(gesuches.iterator().next().getFall());
			}
		}

		final Optional<Benutzer> currentBenutzer = benutzerService.getCurrentBenutzer();
		if (currentBenutzer.isPresent()) {
			final Fall fall = fromTestfall.createFall(currentBenutzer.get());
			final Fall persistedFall = fallService.saveFall(fall);
			fromTestfall.setFall(persistedFall); // dies wird gebraucht, weil fallService.saveFall ein merge macht.

			fromTestfall.createGesuch(LocalDate.of(2016, Month.FEBRUARY, 15));
			gesuchService.createGesuch(fromTestfall.getGesuch());
			Gesuch gesuch = fromTestfall.fillInGesuch();

			final List<WizardStep> wizardStepsFromGesuch = wizardStepService.findWizardStepsFromGesuch(gesuch.getId());

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

			if (verfuegen) {
				verfuegungService.calculateVerfuegung(gesuch);
				gesuch.getKindContainers().stream().forEach(kindContainer -> {
					kindContainer.getBetreuungen().stream().forEach(betreuung -> {
						verfuegungService.persistVerfuegung(betreuung.getVerfuegung(), betreuung.getId());
					});
				});
			}

			wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.VERFUEGEN);

			return gesuch;
		}
		return null;
	}

	private void saveVerfuegungen(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		if (!AntragStatus.VERFUEGT.equals(gesuch.getStatus())) {
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
		if (gesuch.getEinkommensverschlechterungInfo() != null) {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.EINKOMMENSVERSCHLECHTERUNG, WizardStepStatus.IN_BEARBEITUNG);
			einkommensverschlechterungInfoService.createEinkommensverschlechterungInfo(gesuch.getEinkommensverschlechterungInfo());
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
			finanzielleSituationService.saveFinanzielleSituation(gesuch.getGesuchsteller1().getFinanzielleSituationContainer());
		}
		if (gesuch.getGesuchsteller2() != null && gesuch.getGesuchsteller2().getFinanzielleSituationContainer() != null) {
			finanzielleSituationService.saveFinanzielleSituation(gesuch.getGesuchsteller2().getFinanzielleSituationContainer());
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
			-> kindContainer.getBetreuungen().forEach(betreuung -> betreuungService.saveBetreuung(betreuung)));
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
			gesuchstellerService.saveGesuchsteller(gesuch.getGesuchsteller1(), gesuch, 1);
		}
		if (gesuch.getGesuchsteller2() != null) {
			gesuchstellerService.saveGesuchsteller(gesuch.getGesuchsteller2(), gesuch, 2);
		}
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.GESUCHSTELLER);
		// Umzug wird by default OK und verfuegbar, da es nicht notwendig ist, einen Umzug einzutragen
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.UMZUG);
		setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.UMZUG, WizardStepStatus.OK);
	}

	private void saveFamiliensituation(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		if (gesuch.getFamiliensituation() != null) {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.FAMILIENSITUATION, WizardStepStatus.IN_BEARBEITUNG);
			familiensituationService.saveFamiliensituation(gesuch, null, gesuch.getFamiliensituation());
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

}
