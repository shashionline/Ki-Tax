import {EbeguWebCore} from '../../../core/core.module';
import {IStateService} from 'angular-ui-router';
import {BetreuungViewController} from './betreuungView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSBetreuung from '../../../models/TSBetreuung';
import TSInstitutionStammdaten from '../../../models/TSInstitutionStammdaten';
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import {IHttpBackendService, IQService, ITimeoutService} from 'angular';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import TestDataUtil from '../../../utils/TestDataUtil';
import EbeguUtil from '../../../utils/EbeguUtil';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import DateUtil from '../../../utils/DateUtil';
import WizardStepManager from '../../service/wizardStepManager';
import TSKindContainer from '../../../models/TSKindContainer';
import {IBetreuungStateParams} from '../../gesuch.route';
import TSGesuch from '../../../models/TSGesuch';
import {TSAntragTyp} from '../../../models/enums/TSAntragTyp';
import {TSAntragStatus} from '../../../models/enums/TSAntragStatus';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import {TSGesuchsperiodeStatus} from '../../../models/enums/TSGesuchsperiodeStatus';

describe('betreuungView', function () {

    let betreuungView: BetreuungViewController;
    let gesuchModelManager: GesuchModelManager;
    let $state: IStateService;
    let ebeguUtil: EbeguUtil;
    let $q: IQService;
    let betreuung: TSBetreuung;
    let kind: TSKindContainer;
    let $rootScope:  any;
    let $httpBackend: IHttpBackendService;
    let authServiceRS: AuthServiceRS;
    let wizardStepManager: WizardStepManager;
    let $stateParams: IBetreuungStateParams;
    let $timeout: ITimeoutService;


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        $state = $injector.get('$state');
        ebeguUtil = $injector.get('EbeguUtil');
        $httpBackend = $injector.get('$httpBackend');
        $q = $injector.get('$q');
        $stateParams = $injector.get('$stateParams');
        $timeout = $injector.get('$timeout');

        // they always need to be mocked
        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
        TestDataUtil.mockLazyGesuchModelManagerHttpCalls($httpBackend);

        betreuung = new TSBetreuung();
        betreuung.timestampErstellt = DateUtil.today();
        betreuung.betreuungsstatus = TSBetreuungsstatus.AUSSTEHEND;
        kind = new TSKindContainer();
        $stateParams = $injector.get('$stateParams');
        spyOn(gesuchModelManager, 'getKindToWorkWith').and.returnValue(kind);
        spyOn(gesuchModelManager, 'convertKindNumberToKindIndex').and.returnValue(0);
        spyOn(gesuchModelManager, 'isNeuestesGesuch').and.returnValue($q.when(true));
        // model = betreuung;
        spyOn(gesuchModelManager, 'getBetreuungToWorkWith').and.callFake(() => {
             // wenn betreuung view ihr model schon kopiert hat geben wir das zurueck, sonst sind wir noch im constructor der view und geben betreuung zurueck
            return betreuungView ?  betreuungView.model : betreuung;
        });
        $rootScope = $injector.get('$rootScope');
        authServiceRS = $injector.get('AuthServiceRS');
        spyOn(authServiceRS, 'isRole').and.returnValue(true);
        spyOn(authServiceRS, 'isOneOfRoles').and.returnValue(true);
        wizardStepManager = $injector.get('WizardStepManager');
        betreuungView = new BetreuungViewController($state, gesuchModelManager, ebeguUtil, $injector.get('CONSTANTS'),
            $rootScope, $injector.get('BerechnungsManager'), $injector.get('ErrorService'), authServiceRS,
            wizardStepManager, $stateParams, $injector.get('MitteilungRS'), $injector.get('DvDialog'), $injector.get('$log'),
            $timeout);
        betreuungView.$onInit();
        $rootScope.$apply();
        betreuungView.model = betreuung;

        betreuungView.form = TestDataUtil.createDummyForm();
    }));

    describe('Public API', function () {
        it('should include a cancel() function', function () {
            expect(betreuungView.cancel).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('Object creation', () => {
            it('create an empty list of Betreuungspensen for a role different than Institution', () => {
                let myBetreuungView: BetreuungViewController = new BetreuungViewController($state, gesuchModelManager, ebeguUtil, null,
                    $rootScope, null, null, authServiceRS, wizardStepManager, $stateParams, undefined, undefined, undefined, $timeout);
                myBetreuungView.model = betreuung;
                expect(myBetreuungView.getBetreuungspensen()).toBeDefined();
                expect(myBetreuungView.getBetreuungspensen().length).toEqual(0);
            });
        });
        describe('cancel existing object', () => {
            it('should not remove the kind and then go to betreuungen', () => {
                spyOn($state, 'go');
                spyOn(gesuchModelManager, 'removeBetreuungFromKind');
                betreuungView.cancel();
                expect(gesuchModelManager.removeBetreuungFromKind).not.toHaveBeenCalled();
                expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen', {gesuchId: ''});
            });
        });
        describe('cancel non-existing object', () => {
            it('should remove the betreuung from kind and then go to betreuungen', () => {
                spyOn($state, 'go');
                betreuungView.model.timestampErstellt = undefined;
                betreuungView.model.timestampErstellt = undefined;
                spyOn(gesuchModelManager, 'removeBetreuungFromKind');
                betreuungView.cancel();
                expect(gesuchModelManager.removeBetreuungFromKind).toHaveBeenCalled();
                expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen', {gesuchId: ''});
            });
        });
        describe('getInstitutionenSDList', () => {
            beforeEach(function () {
                gesuchModelManager.getActiveInstitutionenList().push(createInstitutionStammdaten('1', TSBetreuungsangebotTyp.KITA));
                gesuchModelManager.getActiveInstitutionenList().push(createInstitutionStammdaten('2', TSBetreuungsangebotTyp.KITA));
                gesuchModelManager.getActiveInstitutionenList().push(createInstitutionStammdaten('3', TSBetreuungsangebotTyp.TAGESELTERN_KLEINKIND));
                gesuchModelManager.getActiveInstitutionenList().push(createInstitutionStammdaten('4', TSBetreuungsangebotTyp.TAGESSCHULE));
            });
            it('should return an empty list if betreuungsangebot is not yet defined', () => {
                let list: Array<TSInstitutionStammdaten> = betreuungView.getInstitutionenSDList();
                expect(list).toBeDefined();
                expect(list.length).toBe(0);
            });
            it('should return a list with 2 Institutions of type TSBetreuungsangebotTyp.KITA', () => {
                betreuungView.betreuungsangebot = {key: 'KITA', value: 'kita'};
                let list: Array<TSInstitutionStammdaten> = betreuungView.getInstitutionenSDList();
                expect(list).toBeDefined();
                expect(list.length).toBe(2);
                expect(list[0].iban).toBe('1');
                expect(list[1].iban).toBe('2');
            });
        });

        /**
         * Some of these tests are usless and don't work anymore.
         */
        describe('createBetreuungspensum', () => {
            it('creates the first betreuungspensum in empty list and then a second one (for role=Institution)', () => {

                expect(betreuungView.getBetreuungspensen()).toBeDefined();
                expect(betreuungView.getBetreuungspensen().length).toBe(0);
                betreuungView.createBetreuungspensum();

                expect(betreuungView.getBetreuungspensen().length).toBe(1);
                expect(betreuungView.getBetreuungspensen()).toBeDefined();
                expect(betreuungView.getBetreuungspensen()[0].betreuungspensumGS).toBeUndefined();
                expect(betreuungView.getBetreuungspensen()[0].betreuungspensumJA).toBeDefined();
                expect(betreuungView.getBetreuungspensen()[0].betreuungspensumJA.pensum).toBeUndefined();
                expect(betreuungView.getBetreuungspensen()[0].betreuungspensumJA.gueltigkeit.gueltigAb).toBeUndefined();
                expect(betreuungView.getBetreuungspensen()[0].betreuungspensumJA.gueltigkeit.gueltigBis).toBeUndefined();
            });
        });
        describe('submit', () => {
            it('submits all data of current Betreuung', () => {
                testSubmit($q.when({}), true);
            });
            it('submits but data are invalid and does not move forward', () => {
                testSubmit($q.reject(), false);
            });
        });
        describe('platzAbweisen()', () => {
            it('must change the status of the Betreuung to ABGEWIESEN and restore initial values of Betreuung', () => {
                spyOn(gesuchModelManager, 'saveBetreuung').and.returnValue($q.when({}));
                spyOn(gesuchModelManager, 'setBetreuungToWorkWith').and.stub();
                betreuungView.model.erweiterteBeduerfnisse = true;
                betreuungView.model.grundAblehnung = 'mein Grund';
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus).toEqual(TSBetreuungsstatus.AUSSTEHEND);
                expect(betreuungView.model.datumAblehnung).toBeUndefined();

                betreuungView.platzAbweisen();

                expect(gesuchModelManager.setBetreuungToWorkWith).toHaveBeenCalledWith(betreuungView.model);
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus).toEqual(TSBetreuungsstatus.ABGEWIESEN);
                expect(gesuchModelManager.getBetreuungToWorkWith().grundAblehnung).toEqual('mein Grund');
                expect(gesuchModelManager.getBetreuungToWorkWith().datumAblehnung).toEqual(DateUtil.today());
                expect(gesuchModelManager.getBetreuungToWorkWith().erweiterteBeduerfnisse).toBe(true);
                expect(gesuchModelManager.saveBetreuung).toHaveBeenCalled();
            });
        });
        describe('platzAnfordern()', () => {
            it('must change the status of the Betreuung to WARTEN', () => {
                spyOn(gesuchModelManager, 'saveBetreuung').and.returnValue($q.when({}));
                betreuungView.model.vertrag = true;
                // betreuung.timestampErstellt = undefined;
                betreuungView.model.betreuungsstatus = TSBetreuungsstatus.AUSSTEHEND;
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus).toEqual(TSBetreuungsstatus.AUSSTEHEND);
                betreuungView.platzAnfordern();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus).toEqual(TSBetreuungsstatus.WARTEN);
                expect(gesuchModelManager.saveBetreuung).toHaveBeenCalled();
            });
        });
        describe('removeBetreuungspensum', () => {
            it('should remove the betreuungspensum from the list', () => {
                betreuungView.getBetreuungModel().betreuungspensumContainers = [];
                betreuungView.createBetreuungspensum();
                expect(betreuungView.getBetreuungspensen().length).toEqual(1);
                betreuungView.removeBetreuungspensum(betreuungView.getBetreuungspensen()[0]);
                expect(betreuungView.getBetreuungspensen().length).toEqual(0);
                betreuungView.createBetreuungspensum();
                expect(betreuungView.getBetreuungspensen().length).toEqual(1);
                betreuungView.removeBetreuungspensum(betreuungView.getBetreuungspensen()[0]);
                expect(betreuungView.getBetreuungspensen().length).toEqual(0);
            });
        });
        describe('isMutationsmeldungAllowed', () => {
            it('should be false if the Gesuch is not a Mutation and is not verfuegt', () => {
                betreuungView.model.betreuungsstatus = TSBetreuungsstatus.BESTAETIGT;
                let gesuch: TSGesuch = initGesuch(TSAntragTyp.ERSTGESUCH, TSAntragStatus.IN_BEARBEITUNG_JA, false);
                expect(betreuungView.isMutationsmeldungAllowed()).toBe(false);
            });
            it('should be true if the Gesuch is not a Mutation but the betreuung is in Status Verfuegt', () => {
                betreuungView.model.betreuungsstatus = TSBetreuungsstatus.VERFUEGT;
                let gesuch: TSGesuch = initGesuch(TSAntragTyp.ERSTGESUCH, TSAntragStatus.VERFUEGT, false);
                expect(betreuungView.isMutationsmeldungAllowed()).toBe(true);
            });
            it('should be false if the Gesuch is not a Mutation and is in Status VERFUEGT but the betreuung is not in Status VERFUEGT', () => {
                // this case is actually not possible
                betreuungView.model.betreuungsstatus = TSBetreuungsstatus.BESTAETIGT;
                let gesuch: TSGesuch = initGesuch(TSAntragTyp.ERSTGESUCH, TSAntragStatus.VERFUEGT, false);
                expect(betreuungView.isMutationsmeldungAllowed()).toBe(false);
            });
            it('should be false if the Gesuch is not a Mutation and is not in Status VERFUEGT but the betreuung is in Status VERFUEGT', () => {
                betreuungView.model.betreuungsstatus = TSBetreuungsstatus.VERFUEGT;
                let gesuch: TSGesuch = initGesuch(TSAntragTyp.ERSTGESUCH, TSAntragStatus.VERFUEGEN, false);
                expect(betreuungView.isMutationsmeldungAllowed()).toBe(false);
            });
            it('should be true if the Gesuch is not gesperrtWegenBeschwerde though STV status and the betreuung is in Status VERFUEGT', () => {
                betreuungView.model.betreuungsstatus = TSBetreuungsstatus.VERFUEGT;
                let gesuch: TSGesuch = initGesuch(TSAntragTyp.ERSTGESUCH, TSAntragStatus.PRUEFUNG_STV, false);
                expect(betreuungView.isMutationsmeldungAllowed()).toBe(true);
            });
            it('should be true if the Gesuch is a Mutation and the betreuung has a vorgaengerID', () => {
                betreuungView.model.vorgaengerId = '111-222-333-444-555';
                let gesuch: TSGesuch = initGesuch(TSAntragTyp.MUTATION, TSAntragStatus.IN_BEARBEITUNG_JA, false);
                expect(betreuungView.isMutationsmeldungAllowed()).toBe(true);
            });
            it('should be false if the Gesuch is a Mutation and the betreuung has no vorgaengerID, i.e. it is a new Betreuung', () => {
                betreuungView.model.vorgaengerId = undefined;
                let gesuch: TSGesuch = initGesuch(TSAntragTyp.MUTATION, TSAntragStatus.IN_BEARBEITUNG_JA, false);
                expect(betreuungView.isMutationsmeldungAllowed()).toBe(false);
            });
            it('should be false if the Mutation is gesperrtWegenBeschwerde although the Betreuung has a vorgaengerId', () => {
                betreuungView.model.vorgaengerId = '111-222-333-444-555';
                let gesuch: TSGesuch = initGesuch(TSAntragTyp.MUTATION, TSAntragStatus.IN_BEARBEITUNG_JA, true);
                expect(betreuungView.isMutationsmeldungAllowed()).toBe(false);
            });
            it('should be true if the Gesuch is a Mutation and is in Status VERFUEGEN but the betreuung is in Status VERFUEGT and has a vorgaengerId', () => {
                betreuungView.model.vorgaengerId = '111-222-333-444-555';
                betreuungView.model.betreuungsstatus = TSBetreuungsstatus.VERFUEGT;
                let gesuch: TSGesuch = initGesuch(TSAntragTyp.MUTATION, TSAntragStatus.VERFUEGEN, false);
                expect(betreuungView.isMutationsmeldungAllowed()).toBe(true);
            });
            it('should be true if the Gesuch is a Mutation and is in Status VERFUEGT and the betreuung is in Status VERFUEGT but has no vorgaengerId', () => {
                betreuungView.model.vorgaengerId = undefined;
                betreuungView.model.betreuungsstatus = TSBetreuungsstatus.VERFUEGT;
                let gesuch: TSGesuch = initGesuch(TSAntragTyp.MUTATION, TSAntragStatus.VERFUEGT, false);
                expect(betreuungView.isMutationsmeldungAllowed()).toBe(true);
            });
        });
    });

    function initGesuch(typ: TSAntragTyp, status: TSAntragStatus, gesperrtWegenBeschwerde: boolean): TSGesuch {
        let gesuch: TSGesuch = new TSGesuch();
        gesuch.typ = typ;
        gesuch.status = status;
        gesuch.gesperrtWegenBeschwerde = gesperrtWegenBeschwerde;
        gesuch.gesuchsperiode = new TSGesuchsperiode();
        gesuch.gesuchsperiode.status = TSGesuchsperiodeStatus.AKTIV;
        spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
        return gesuch;
    }

    function createInstitutionStammdaten(iban: string, betAngTyp: TSBetreuungsangebotTyp) {
        let instStam1: TSInstitutionStammdaten = new TSInstitutionStammdaten();
        instStam1.iban = iban;
        instStam1.betreuungsangebotTyp = betAngTyp;
        return instStam1;
    }

    /**
     * Das Parameter promiseResponse ist das Object das die Methode gesuchModelManager.saveBetreuung() zurueckgeben muss. Wenn dieses
     * eine Exception (reject) ist, muss der $state nicht geaendert werden und daher wird die Methode $state.go()  nicht aufgerufen.
     * Ansonsten wird sie mit  dem naechsten state 'gesuch.betreuungen' aufgerufen
     * @param promiseResponse
     * @param moveToNextStep
     */
    function testSubmit(promiseResponse: any, moveToNextStep: boolean) {
        betreuungView.model.vertrag = true;
        spyOn($state, 'go');
        spyOn(gesuchModelManager, 'saveBetreuung').and.returnValue(promiseResponse);
        betreuungView.platzAnfordern();
        $rootScope.$apply();
        expect(gesuchModelManager.saveBetreuung).toHaveBeenCalled();
        if (moveToNextStep) {
            expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen', { gesuchId: '' });
        } else {
            expect($state.go).not.toHaveBeenCalled();
        }
    }

});
