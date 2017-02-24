package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Zahlung;
import ch.dvbern.ebegu.entities.Zahlungsauftrag;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Zahlungen
 */
public interface ZahlungService {

	/**
	 * Ermittelt alle im aktuellen Monat gueltigen Verfuegungen, sowie aller seit dem letzten Auftrag eingeganegenen
	 * Mutationen.
	 * Der Zahlungsauftrag hat den initialen Status ENTWURF
	 */
	Zahlungsauftrag zahlungsauftragErstellen(LocalDate datumFaelligkeit, String beschreibung);

	/**
	 * Aktualisiert das Fälligkeitsdatum und die Beschreibung im übergebenen Auftrag. Die Zahlungspositionen werden
	 * *nicht* neu generiert
	 */
	Zahlungsauftrag zahlungsauftragAktualisieren(String auftragId, LocalDate datumFaelligkeit, String beschreibung);

	/**
	 * Nachdem alle Daten kontrolliert wurden, wird der Zahlungsauftrag ausgeloest. Danach kann er nicht mehr
	 * geloescht werden
	 */
	Zahlungsauftrag zahlungsauftragAusloesen(String auftragId);

	/**
	 * Sucht einen einzelnen Zahlungsauftrag.
	 * TODO (team) im JaxBConverter aufgrund Berechtigung des Benutzers Zahlungen entfernen!
	 */
	Optional<Zahlungsauftrag> findZahlungsauftrag(String auftragId);

	/**
	 * Loescht einen Zahlungsauftrag (nur im Status ENTWURF moeglich)
	 */
	void deleteZahlungsauftrag(String auftragId);

	/**
	 * Gibt alle Zahlungsauftraege zurueck TODO (team) evt. muessen wir dann hier einschraenken, sonst waechst die liste unendlich...
	 * TODO (team) im JaxBConverter aufgrund Berechtigung des Benutzers Zahlungen entfernen!
	 */
	Collection<Zahlungsauftrag> getAllZahlungsauftraege();

	/**
	 * Erstellt ein ISO-20022-File mit den Zahlunspositionen des gewaehlten Auftrags.
	 */
	String createIsoFile(String auftragId);

	/**
	 * Eine Kita kann/muss den Zahlungseingang bestaetigen
	 */
	Zahlung zahlungBestaetigen(String zahlungId);


}