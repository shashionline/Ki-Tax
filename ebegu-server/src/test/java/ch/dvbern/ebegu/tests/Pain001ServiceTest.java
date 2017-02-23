package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.ZahlungStatus;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.iso20022.V03CH02.Document;
import ch.dvbern.ebegu.services.InstitutionStammdatenService;
import ch.dvbern.ebegu.services.Pain001Service;
import ch.dvbern.ebegu.services.Pain001ServiceBean;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.dvbern.ebegu.services.Pain001Service.SCHEMA_LOCATION;
import static ch.dvbern.ebegu.services.Pain001Service.SCHEMA_NAME;

/**
 * Tests fuer die Klasse Pain001Service
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/mandant-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class Pain001ServiceTest extends AbstractEbeguLoginTest {

	@Inject
	private Pain001Service pain001Service;

	@Inject
	private InstitutionStammdatenService institutionStammdatenService;


	@Inject
	private Persistence<?> persistence;

	Collection<InstitutionStammdaten> allInstitutionStammdaten;


	@Before
	public void init() {
		final Gesuchsperiode gesuchsperiode = createGesuchsperiode(true);
		final Mandant mandant = insertInstitutionen();
		//createBenutzer(mandant);
		TestDataUtil.prepareApplicationProperties(persistence);

		allInstitutionStammdaten = institutionStammdatenService.getAllInstitutionStammdaten();
	}

	@Test
	public void getPainFileContentTest() {
		Assert.assertNotNull(pain001Service);

		List<Zahlung> zahlungList = new ArrayList<>();

		for (InstitutionStammdaten stammdaten : allInstitutionStammdaten) {
			Zahlung zahlung = new Zahlung();
			zahlung.setInstitutionStammdaten(stammdaten);
			zahlung.setStatus(ZahlungStatus.AUSGELOEST);
			zahlungList.add(zahlung);
		}

		Zahlungsauftrag zahlungsauftrag = new Zahlungsauftrag();
		zahlungsauftrag.setDatumFaellig(LocalDate.now());
		zahlungsauftrag.setZahlungen(zahlungList);

		final byte[] painFileContent = pain001Service.getPainFileContent(zahlungsauftrag);

		Assert.assertNotNull(painFileContent);

		ByteArrayInputStream bis = new ByteArrayInputStream(painFileContent);
		final Document document = getDocumentFromInputStream(bis);
		Assert.assertNotNull(document);


	}


	private Document getDocumentFromInputStream(ByteArrayInputStream bis) {
		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);


			final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			// output pretty printed
			jaxbUnmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbUnmarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SCHEMA_LOCATION + " " + SCHEMA_NAME);

			//noinspection Convert2Lambda: Hier bitte nicht lambda verwenden, es gibt teilweise Fehler mit Java-Version
			jaxbUnmarshaller.setEventHandler(new Pain001ServiceTest.PainValidationEventHandler());

			return (Document) jaxbUnmarshaller.unmarshal(bis);

		} catch (final Exception e) {
			Assert.assertTrue("Failed to marshal Document",true);
		}
		return null;
	}

	private static class PainValidationEventHandler implements ValidationEventHandler {
		@Override
		public boolean handleEvent(ValidationEvent event) {
			throw new EbeguRuntimeException("Unerwarteter Fehler beim generieren des Zahlungsfile", event.getMessage(), event.getLinkedException());
		}
	}

	private JAXBElement<Document> getElementToMarshall(Document elemToMarshall) {
		return new JAXBElement<>(new QName(SCHEMA_LOCATION, elemToMarshall.getClass().getSimpleName()), Document.class, elemToMarshall);
	}
}
