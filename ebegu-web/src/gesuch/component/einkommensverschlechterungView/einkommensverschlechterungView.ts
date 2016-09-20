import {IComponentOptions, ILogService, IPromise} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IEinkommensverschlechterungStateParams} from '../../gesuch.route';
import BerechnungsManager from '../../service/berechnungsManager';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import ErrorService from '../../../core/errors/service/ErrorService';
import TSEinkommensverschlechterung from '../../../models/TSEinkommensverschlechterung';
import TSFinanzielleSituation from '../../../models/TSFinanzielleSituation';
import WizardStepManager from '../../service/wizardStepManager';
import TSEinkommensverschlechterungContainer from '../../../models/TSEinkommensverschlechterungContainer';
import {TSRole} from '../../../models/enums/TSRole';
let template = require('./einkommensverschlechterungView.html');
require('./einkommensverschlechterungView.less');


export class EinkommensverschlechterungViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = EinkommensverschlechterungViewController;
    controllerAs = 'vm';
}

export class EinkommensverschlechterungViewController extends AbstractGesuchViewController {

    public showSelbstaendig: boolean;
    public geschaeftsgewinnBasisjahrMinus1: number;
    public geschaeftsgewinnBasisjahrMinus2: number;
    allowedRoles: Array<TSRole>;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', '$log',
        'WizardStepManager'];

    /* @ngInject */
    constructor($stateParams: IEinkommensverschlechterungStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService, private $log: ILogService,
                wizardStepManager: WizardStepManager) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        let parsedGesuchstelllerNum: number = parseInt($stateParams.gesuchstellerNumber, 10);
        let parsedBasisJahrPlusNum: number = parseInt($stateParams.basisjahrPlus, 10);
        this.gesuchModelManager.setGesuchstellerNumber(parsedGesuchstelllerNum);
        this.gesuchModelManager.setBasisJahrPlusNumber(parsedBasisJahrPlusNum);
        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
        this.initViewModel();
        this.calculate();

    }

    private initViewModel() {
        if (this.gesuchModelManager) {
            this.gesuchModelManager.initEinkommensverschlechterungContainer(this.gesuchModelManager.getBasisJahrPlusNumber(),
                this.gesuchModelManager.getGesuchstellerNumber());

            this.getGeschaeftsgewinnFromFS();

            this.showSelbstaendig = this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationJA.isSelbstaendig()
                || (this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().geschaeftsgewinnBasisjahr !== null
                && this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().geschaeftsgewinnBasisjahr !== undefined);
        }
    }

    public showSelbstaendigClicked() {
        if (!this.showSelbstaendig) {
            this.resetSelbstaendigFields();
        }
    }

    private resetSelbstaendigFields() {
        if (this.gesuchModelManager.getEinkommensverschlechterungToWorkWith()) {
            this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().geschaeftsgewinnBasisjahr = undefined;
            this.calculate();
        }
    }

    showSteuerveranlagung(): boolean {
        return !this.gesuchModelManager.getGemeinsameSteuererklaerungToWorkWith() || this.gesuchModelManager.getGemeinsameSteuererklaerungToWorkWith() === false;
    }

    showSteuererklaerung(): boolean {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === false;
    }

    showHintSteuererklaerung(): boolean {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuererklaerungAusgefuellt === true &&
            this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === false;
    }

    showHintSteuerveranlagung(): boolean {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === true;
    }

    steuerveranlagungClicked(): void {
        // Wenn Steuerveranlagung JA -> auch StekErhalten -> JA
        if (this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === true) {
            this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuererklaerungAusgefuellt = true;
        } else if (this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === false) {
            // Steuerveranlagung neu NEIN -> Fragen loeschen
            this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuererklaerungAusgefuellt = undefined;
        }
    }

    private save(form: angular.IFormController): IPromise<TSEinkommensverschlechterungContainer> {
        if (form.$valid) {
            this.errorService.clearAll();
            return this.gesuchModelManager.saveEinkommensverschlechterungContainer();
        }
        return undefined;
    }

    calculate() {
        this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.getGesuch(), this.gesuchModelManager.getBasisJahrPlusNumber());
    }

    resetForm() {
        this.initViewModel();
    }

    public getEinkommensverschlechterung(): TSEinkommensverschlechterung {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith();
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        return this.berechnungsManager.getEinkommensverschlechterungResultate(this.gesuchModelManager.getBasisJahrPlusNumber());
    }

    public getGeschaeftsgewinnFromFS(): void {
        if (!this.gesuchModelManager.getStammdatenToWorkWith() || !this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer
            || !this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationJA) {
            // TODO: Wenn die finanzielleSituation noch nicht existiert haben wir ein Problem
            this.$log.debug('Fehler: FinSit muss existieren');
            return;
        }

        let fs: TSFinanzielleSituation = this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationJA;
        if (this.gesuchModelManager.basisJahrPlusNumber === 1) {
            this.geschaeftsgewinnBasisjahrMinus1 = fs.geschaeftsgewinnBasisjahr;
            this.geschaeftsgewinnBasisjahrMinus2 = fs.geschaeftsgewinnBasisjahrMinus1;
        } else {
            //basisjahr Plus 2
            this.geschaeftsgewinnBasisjahrMinus1 = this.gesuchModelManager.getStammdatenToWorkWith().einkommensverschlechterungContainer.ekvJABasisJahrPlus1.geschaeftsgewinnBasisjahr;
            this.geschaeftsgewinnBasisjahrMinus2 = fs.geschaeftsgewinnBasisjahr;
        }
    }

}
