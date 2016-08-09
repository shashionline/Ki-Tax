package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.services.DokumentGrundService;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Tests fuer die Klasse DokumentGrundService
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class DokumentGrundServiceTest extends AbstractEbeguTest {

	@Inject
	private DokumentGrundService dokumentGrundService;

	@Inject
	private Persistence<DokumentGrund> persistence;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {
		return createTestArchive();
	}

	@Test
	public void createDokumentGrund() {
		Assert.assertNotNull(dokumentGrundService);
		
		DokumentGrund dokumentGrund = TestDataUtil.createDefaultDokumentGrund();
		Gesuch gesuch = TestDataUtil.createDefaultGesuch();
		persistence.persist(gesuch.getGesuchsperiode());
		persistence.persist(gesuch.getFall());
		persistence.persist(gesuch);
		dokumentGrund.setGesuch(gesuch);

		dokumentGrundService.saveDokumentGrund(dokumentGrund);
		Optional<DokumentGrund> dokumentGrundOpt = dokumentGrundService.findDokumentGrund(dokumentGrund.getId());
		Assert.assertTrue(dokumentGrundOpt.isPresent());
		Assert.assertEquals(dokumentGrund.getFullName(), dokumentGrundOpt.get().getFullName());
	}

}