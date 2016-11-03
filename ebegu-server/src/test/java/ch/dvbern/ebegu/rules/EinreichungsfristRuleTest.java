package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static ch.dvbern.ebegu.rules.EbeguRuleTestsHelper.calculate;

/**
 * Tests für Einreichungsfrist-Regel
 */
public class EinreichungsfristRuleTest {


	private final LocalDate START_PERIODE = LocalDate.of(2016, Month.AUGUST, 1);
	private final LocalDate ENDE_PERIODE = LocalDate.of(2017, Month.JULY, 31);

	/**
	 * Kita: Einreichung am 1.2., Start der Betreuung am 1.8.
	 */
	@Test
	public void testKitaEinreichungRechtzeitig() {
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE,  BetreuungsangebotTyp.KITA, 60);
		final Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		gesuch.setEingangsdatum(LocalDate.of(2016, Month.FEBRUARY, 1));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);
		List<VerfuegungZeitabschnitt> nextRestanspruch = EbeguRuleTestsHelper.initializeRestanspruchForNextBetreuung(betreuung, result);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Integer.valueOf(60), result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(60, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, result.get(0).getBgPensum());
		Assert.assertEquals(0, nextRestanspruch.get(0).getAnspruchspensumRest());
		Assert.assertEquals(false, result.get(0).isZuSpaetEingereicht());
		Assert.assertEquals(false, result.get(0).isBezahltVollkosten());
	}

	/**
	 * Kita: Einreichung am 7.10., Start der Betreuung am 1.8.
	 */
	@Test
	public void testKitaEinreichungZuSpaet() {
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE,  BetreuungsangebotTyp.KITA, 60);
		final Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		gesuch.setEingangsdatum(LocalDate.of(2016, Month.OCTOBER, 7));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);
		List<VerfuegungZeitabschnitt> nextRestanspruch = EbeguRuleTestsHelper.initializeRestanspruchForNextBetreuung(betreuung, result);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());

		VerfuegungZeitabschnitt abschnitt1 = result.get(0);
		Assert.assertEquals(Integer.valueOf(60), abschnitt1.getErwerbspensumGS1());
		Assert.assertEquals(60, abschnitt1.getBetreuungspensum());
		Assert.assertEquals(0, abschnitt1.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnitt1.getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		Assert.assertEquals(0, nextRestanspruch.get(0).getAnspruchspensumRest());
		Assert.assertEquals(true, abschnitt1.isZuSpaetEingereicht());
		Assert.assertEquals(false, abschnitt1.isBezahltVollkosten());

		VerfuegungZeitabschnitt abschnitt2 = result.get(1);
		Assert.assertEquals(Integer.valueOf(60), abschnitt2.getErwerbspensumGS1());
		Assert.assertEquals(60, abschnitt2.getBetreuungspensum());
		Assert.assertEquals(60, abschnitt2.getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, abschnitt2.getBgPensum());
		Assert.assertEquals(-1, abschnitt2.getAnspruchspensumRest());
		Assert.assertEquals(0, nextRestanspruch.get(1).getAnspruchspensumRest());
		Assert.assertEquals(false, abschnitt2.isZuSpaetEingereicht());
		Assert.assertEquals(false, abschnitt2.isBezahltVollkosten());
	}

	/**
	 * Kita: Einreichung am 7.8., Start der Betreuung am 1.8.
	 */
	@Test
	public void testKitaEinreichungInGnadenfrist() {
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE,  BetreuungsangebotTyp.KITA, 60);
		final Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		gesuch.setEingangsdatum(LocalDate.of(2016, Month.AUGUST, 7));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);
		List<VerfuegungZeitabschnitt> nextRestanspruch = EbeguRuleTestsHelper.initializeRestanspruchForNextBetreuung(betreuung, result);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Integer.valueOf(60), result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(60, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, result.get(0).getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		Assert.assertEquals(0, nextRestanspruch.get(0).getAnspruchspensumRest());
		Assert.assertEquals(false, result.get(0).isZuSpaetEingereicht());
		Assert.assertEquals(false, result.get(0).isBezahltVollkosten());
	}

	/**
	 * Kita: Einreichung am 7.8., Start der Betreuung am 5.8.
	 */
	@Test
	public void testKitaEinreichungInZuSpaetAberNachDemErsten() {
		LocalDate betreuungsStart = LocalDate.of(2016, Month.AUGUST, 8);
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(betreuungsStart, ENDE_PERIODE,  BetreuungsangebotTyp.KITA, 60);
		final Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		gesuch.setEingangsdatum(LocalDate.of(2016, Month.AUGUST, 7));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);
		List<VerfuegungZeitabschnitt> nextRestanspruch = EbeguRuleTestsHelper.initializeRestanspruchForNextBetreuung(betreuung, result);

		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());

		VerfuegungZeitabschnitt abschnitt2 = result.get(1);
		Assert.assertEquals(betreuungsStart, abschnitt2.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(Integer.valueOf(60), abschnitt2.getErwerbspensumGS1());
		Assert.assertEquals(60, abschnitt2.getBetreuungspensum());
		Assert.assertEquals(60, abschnitt2.getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, abschnitt2.getBgPensum());
		Assert.assertEquals(0, nextRestanspruch.get(1).getAnspruchspensumRest());
		Assert.assertEquals(false, abschnitt2.isZuSpaetEingereicht());
		Assert.assertEquals(false, abschnitt2.isBezahltVollkosten());
	}

	/**
	 * Tagi: Einreichung am 1.2., Start der Betreuung am 1.8.
	 */
	@Test
	public void testTagiEinreichungRechtzeitig() {
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE,  BetreuungsangebotTyp.TAGI, 60);
		final Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		gesuch.setEingangsdatum(LocalDate.of(2016, Month.FEBRUARY, 1));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Integer.valueOf(60), result.get(0).getErwerbspensumGS1());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(60, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, result.get(0).getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		List<VerfuegungZeitabschnitt> nextRestanspruch = EbeguRuleTestsHelper.initializeRestanspruchForNextBetreuung(betreuung, result);
		Assert.assertEquals(-1, nextRestanspruch.get(0).getAnspruchspensumRest());
		Assert.assertEquals(false, result.get(0).isZuSpaetEingereicht());
		Assert.assertEquals(false, result.get(0).isBezahltVollkosten());
	}

	/**
	 * Tagi: Einreichung am 7.10., Start der Betreuung am 1.8.
	 */
	@Test
	public void testTagiEinreichungZuSpaet() {
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE,  BetreuungsangebotTyp.TAGI, 60);
		final Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		gesuch.setEingangsdatum(LocalDate.of(2016, Month.OCTOBER, 7));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);
		List<VerfuegungZeitabschnitt> nextRestanspruch = EbeguRuleTestsHelper.initializeRestanspruchForNextBetreuung(betreuung, result);

		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());

		VerfuegungZeitabschnitt abschnitt1 = result.get(0);
		Assert.assertEquals(Integer.valueOf(60), abschnitt1.getErwerbspensumGS1());
		Assert.assertEquals(60, abschnitt1.getBetreuungspensum());
		Assert.assertEquals(60, abschnitt1.getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, abschnitt1.getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		Assert.assertEquals(-1, nextRestanspruch.get(0).getAnspruchspensumRest());
		Assert.assertEquals(true, abschnitt1.isZuSpaetEingereicht());
		Assert.assertEquals(true, abschnitt1.isBezahltVollkosten());

		VerfuegungZeitabschnitt abschnitt2 = result.get(1);
		Assert.assertEquals(Integer.valueOf(60), abschnitt2.getErwerbspensumGS1());
		Assert.assertEquals(60, abschnitt2.getBetreuungspensum());
		Assert.assertEquals(60, abschnitt2.getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, abschnitt2.getBgPensum());
		Assert.assertEquals(-1, nextRestanspruch.get(1).getAnspruchspensumRest());
		Assert.assertEquals(false, abschnitt2.isZuSpaetEingereicht());
		Assert.assertEquals(false, abschnitt2.isBezahltVollkosten());
	}

	/**
	 * Tagi: Einreichung am 7.8., Start der Betreuung am 1.8.
	 */
	@Test
	public void testTagiEinreichungInGnadenfrist() {
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(START_PERIODE, ENDE_PERIODE,  BetreuungsangebotTyp.TAGI, 60);
		final Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		gesuch.setEingangsdatum(LocalDate.of(2016, Month.AUGUST, 7));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Integer.valueOf(60), result.get(0).getErwerbspensumGS1());
		Assert.assertNull(result.get(0).getErwerbspensumGS2());
		Assert.assertEquals(60, result.get(0).getBetreuungspensum());
		Assert.assertEquals(60, result.get(0).getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, result.get(0).getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		List<VerfuegungZeitabschnitt> nextRestanspruch = EbeguRuleTestsHelper.initializeRestanspruchForNextBetreuung(betreuung, result);
		Assert.assertEquals(-1, nextRestanspruch.get(0).getAnspruchspensumRest());
		Assert.assertEquals(false, result.get(0).isZuSpaetEingereicht());
		Assert.assertEquals(false, result.get(0).isBezahltVollkosten());
	}

	/**
	 * Tagi: Einreichung am 7.8., Start der Betreuung am 5.8.
	 */
	@Test
	public void testTagiEinreichungInZuSpaetAberNachDemErsten() {
		LocalDate betreuungsStart = LocalDate.of(2016, Month.AUGUST, 8);
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(betreuungsStart, ENDE_PERIODE,  BetreuungsangebotTyp.TAGI, 60);
		final Gesuch gesuch = betreuung.extractGesuch();
		TestDataUtil.createDefaultAdressenForGS(gesuch, false);
		gesuch.getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, ENDE_PERIODE, 60, 0));
		gesuch.setEingangsdatum(LocalDate.of(2016, Month.AUGUST, 7));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);

		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());

		VerfuegungZeitabschnitt abschnitt2 = result.get(1);
		Assert.assertEquals(betreuungsStart, abschnitt2.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(Integer.valueOf(60), abschnitt2.getErwerbspensumGS1());
		Assert.assertNull(abschnitt2.getErwerbspensumGS2());
		Assert.assertEquals(60, abschnitt2.getBetreuungspensum());
		Assert.assertEquals(60, abschnitt2.getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, abschnitt2.getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		List<VerfuegungZeitabschnitt> nextRestanspruch = EbeguRuleTestsHelper.initializeRestanspruchForNextBetreuung(betreuung, result);
		Assert.assertEquals(-1, nextRestanspruch.get(0).getAnspruchspensumRest());
		Assert.assertEquals(false, abschnitt2.isZuSpaetEingereicht());
		Assert.assertEquals(false, abschnitt2.isBezahltVollkosten());
	}

	/**
	 * Kita: Einreichung am 7.10., Start der Betreuung am 1.8.
	 */
	@Test
	public void testKitaBetreuungspensumInnerhalbGesuchsperiode() {
		final LocalDate betreuungStart = LocalDate.of(START_PERIODE.getYear(), 12, 20);
		final LocalDate betreuungEnde = LocalDate.of(START_PERIODE.plusYears(1).getYear(), 3, 15);
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(
			betreuungStart, betreuungEnde, BetreuungsangebotTyp.KITA, 60);
		LocalDate pensumAEnd = START_PERIODE.plusMonths(1).minusDays(1);
		LocalDate pensumBStart = pensumAEnd.plusDays(1);
		betreuung.extractGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, pensumAEnd, 50, 0));
		betreuung.extractGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(pensumBStart, ENDE_PERIODE, 60, 0));

		betreuung.extractGesuch().setEingangsdatum(LocalDate.of(2016, Month.OCTOBER, 7));
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);
		List<VerfuegungZeitabschnitt> nextRestanspruch = EbeguRuleTestsHelper.initializeRestanspruchForNextBetreuung(betreuung, result);
		Assert.assertNotNull(result);
		Assert.assertEquals(4, result.size());

		VerfuegungZeitabschnitt abschnitt0 = result.get(0);
		Assert.assertEquals(START_PERIODE, abschnitt0.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(pensumAEnd, abschnitt0.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(50), abschnitt0.getErwerbspensumGS1());
		Assert.assertNull(abschnitt0.getErwerbspensumGS2());
		Assert.assertEquals(0, abschnitt0.getBetreuungspensum());
		Assert.assertEquals(50, abschnitt0.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnitt0.getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		Assert.assertEquals(50, nextRestanspruch.get(0).getAnspruchspensumRest());
		Assert.assertEquals(false, abschnitt0.isZuSpaetEingereicht());
		Assert.assertEquals(false, abschnitt0.isBezahltVollkosten());

		VerfuegungZeitabschnitt abschnitt1 = result.get(1);
		Assert.assertEquals(pensumBStart, abschnitt1.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(betreuungStart.minusDays(1), abschnitt1.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(60), abschnitt1.getErwerbspensumGS1());
		Assert.assertNull(abschnitt1.getErwerbspensumGS2());
		Assert.assertEquals(0, abschnitt1.getBetreuungspensum());
		Assert.assertEquals(60, abschnitt1.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnitt1.getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		Assert.assertEquals(60, nextRestanspruch.get(1).getAnspruchspensumRest());
		Assert.assertEquals(false, abschnitt1.isZuSpaetEingereicht());
		Assert.assertEquals(false, abschnitt1.isBezahltVollkosten());

		VerfuegungZeitabschnitt abschnitt2 = result.get(2);
		Assert.assertEquals(betreuungStart, abschnitt2.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(betreuungEnde, abschnitt2.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(60), abschnitt2.getErwerbspensumGS1());
		Assert.assertNull(abschnitt2.getErwerbspensumGS2());
		Assert.assertEquals(60, abschnitt2.getBetreuungspensum());
		Assert.assertEquals(60, abschnitt2.getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, abschnitt2.getBgPensum());
		Assert.assertEquals(-1, abschnitt2.getAnspruchspensumRest());
		Assert.assertEquals(0, nextRestanspruch.get(2).getAnspruchspensumRest());
		Assert.assertEquals(false, abschnitt2.isZuSpaetEingereicht());
		Assert.assertEquals(false, abschnitt2.isBezahltVollkosten());

		VerfuegungZeitabschnitt abschnitt3 = result.get(3);
		Assert.assertEquals(betreuungEnde.plusDays(1), abschnitt3.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(ENDE_PERIODE, abschnitt3.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(60), abschnitt3.getErwerbspensumGS1());
		Assert.assertNull(abschnitt3.getErwerbspensumGS2());
		Assert.assertEquals(0, abschnitt3.getBetreuungspensum());
		Assert.assertEquals(60, abschnitt3.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnitt3.getBgPensum());
		Assert.assertEquals(-1, abschnitt3.getAnspruchspensumRest());
		Assert.assertEquals(60, nextRestanspruch.get(3).getAnspruchspensumRest());
		Assert.assertEquals(false, abschnitt3.isZuSpaetEingereicht());
		Assert.assertEquals(false, abschnitt3.isBezahltVollkosten());
	}

	/**
	 * Kita: Einreichung am 7.10., Start der Betreuung am 1.8.
	 */
	@Test
	public void testKitaBetreuungspensumInnerhalbGesuchsperiodeZuSpaetEingereicht() {
		final LocalDate betreuungStart = LocalDate.of(START_PERIODE.getYear(), 12, 20);
		final LocalDate betreuungEnde = LocalDate.of(START_PERIODE.plusYears(1).getYear(), 3, 15);
		Betreuung betreuung = EbeguRuleTestsHelper.createBetreuungWithPensum(
			betreuungStart, betreuungEnde, BetreuungsangebotTyp.KITA, 60);
		LocalDate pensumAEnd = START_PERIODE.plusMonths(1).minusDays(1);
		LocalDate pensumBStart = pensumAEnd.plusDays(1);
		betreuung.extractGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(START_PERIODE, pensumAEnd, 50, 0));
		betreuung.extractGesuch().getGesuchsteller1().addErwerbspensumContainer(TestDataUtil.createErwerbspensum(pensumBStart, ENDE_PERIODE, 60, 0));

		final LocalDate eingangsdatum = LocalDate.of(2017, Month.JANUARY, 10);
		betreuung.extractGesuch().setEingangsdatum(eingangsdatum);
		List<VerfuegungZeitabschnitt> result = calculate(betreuung);
		List<VerfuegungZeitabschnitt> nextRestanspruch = EbeguRuleTestsHelper.initializeRestanspruchForNextBetreuung(betreuung, result);
		Assert.assertNotNull(result);
		Assert.assertEquals(5, result.size());

		VerfuegungZeitabschnitt abschnitt0 = result.get(0);
		Assert.assertEquals(START_PERIODE, abschnitt0.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(pensumAEnd, abschnitt0.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(50), abschnitt0.getErwerbspensumGS1());
		Assert.assertNull(abschnitt0.getErwerbspensumGS2());
		Assert.assertEquals(0, abschnitt0.getBetreuungspensum());
		Assert.assertEquals(0, abschnitt0.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnitt0.getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		Assert.assertEquals(0, nextRestanspruch.get(0).getAnspruchspensumRest());
		Assert.assertEquals(true, abschnitt0.isZuSpaetEingereicht());
		Assert.assertEquals(false, abschnitt0.isBezahltVollkosten());

		VerfuegungZeitabschnitt abschnitt1 = result.get(1);
		Assert.assertEquals(pensumBStart, abschnitt1.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(betreuungStart.minusDays(1), abschnitt1.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(60), abschnitt1.getErwerbspensumGS1());
		Assert.assertNull(abschnitt1.getErwerbspensumGS2());
		Assert.assertEquals(0, abschnitt1.getBetreuungspensum());
		Assert.assertEquals(0, abschnitt1.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnitt1.getBgPensum());
		Assert.assertEquals(-1, result.get(0).getAnspruchspensumRest());
		Assert.assertEquals(0, nextRestanspruch.get(1).getAnspruchspensumRest());
		Assert.assertEquals(true, abschnitt1.isZuSpaetEingereicht());
		Assert.assertEquals(false, abschnitt1.isBezahltVollkosten());

		VerfuegungZeitabschnitt abschnitt2 = result.get(2);
		Assert.assertEquals(betreuungStart, abschnitt2.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(eingangsdatum.minusMonths(1).withDayOfMonth(31), abschnitt2.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(60), abschnitt2.getErwerbspensumGS1());
		Assert.assertNull(abschnitt2.getErwerbspensumGS2());
		Assert.assertEquals(60, abschnitt2.getBetreuungspensum());
		Assert.assertEquals(0, abschnitt2.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnitt2.getBgPensum());
		Assert.assertEquals(-1, abschnitt2.getAnspruchspensumRest());
		Assert.assertEquals(0, nextRestanspruch.get(2).getAnspruchspensumRest());
		Assert.assertEquals(true, abschnitt2.isZuSpaetEingereicht());
		Assert.assertEquals(false, abschnitt2.isBezahltVollkosten());

		VerfuegungZeitabschnitt abschnitt3 = result.get(3);
		Assert.assertEquals(eingangsdatum.withDayOfMonth(1), abschnitt3.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(betreuungEnde, abschnitt3.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(60), abschnitt3.getErwerbspensumGS1());
		Assert.assertNull(abschnitt3.getErwerbspensumGS2());
		Assert.assertEquals(60, abschnitt3.getBetreuungspensum());
		Assert.assertEquals(60, abschnitt3.getAnspruchberechtigtesPensum());
		Assert.assertEquals(60, abschnitt3.getBgPensum());
		Assert.assertEquals(-1, abschnitt3.getAnspruchspensumRest());
		Assert.assertEquals(0, nextRestanspruch.get(3).getAnspruchspensumRest());
		Assert.assertEquals(false, abschnitt3.isZuSpaetEingereicht());
		Assert.assertEquals(false, abschnitt3.isBezahltVollkosten());

		VerfuegungZeitabschnitt abschnitt4 = result.get(4);
		Assert.assertEquals(betreuungEnde.plusDays(1), abschnitt4.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(ENDE_PERIODE, abschnitt4.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(Integer.valueOf(60), abschnitt4.getErwerbspensumGS1());
		Assert.assertNull(abschnitt4.getErwerbspensumGS2());
		Assert.assertEquals(0, abschnitt4.getBetreuungspensum());
		Assert.assertEquals(60, abschnitt4.getAnspruchberechtigtesPensum());
		Assert.assertEquals(0, abschnitt4.getBgPensum());
		Assert.assertEquals(-1, abschnitt4.getAnspruchspensumRest());
		Assert.assertEquals(60, nextRestanspruch.get(4).getAnspruchspensumRest());
		Assert.assertEquals(false, abschnitt4.isZuSpaetEingereicht());
		Assert.assertEquals(false, abschnitt4.isBezahltVollkosten());
	}

}
