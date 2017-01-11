import {IComponentOptions, IPromise} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import TSGesuch from '../../../models/TSGesuch';
import ErrorService from '../../../core/errors/service/ErrorService';
import {INewFallStateParams} from '../../gesuch.route';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSAntragTyp} from '../../../models/enums/TSAntragTyp';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {TSEingangsart} from '../../../models/enums/TSEingangsart';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import Moment = moment.Moment;
import ITranslateService = angular.translate.ITranslateService;
import IQService = angular.IQService;
import IScope = angular.IScope;
let template = require('./fallCreationView.html');
require('./fallCreationView.less');

export class FallCreationViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FallCreationViewController;
    controllerAs = 'vm';
}

export class FallCreationViewController extends AbstractGesuchViewController<any> {
    private gesuchsperiodeId: string;
    private createNewParam: boolean = false;
    private createMutation: boolean = false;
    private eingangsart: TSEingangsart;
    private fallId: string;

    TSRoleUtil: any;

    // showError ist ein Hack damit, die Fehlermeldung fuer die Checkboxes nicht direkt beim Laden der Seite angezeigt wird
    // sondern erst nachdem man auf ein checkbox oder auf speichern geklickt hat
    showError: boolean = false;

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'ErrorService', '$stateParams',
        'WizardStepManager', '$translate', '$q', '$scope' , 'AuthServiceRS'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private errorService: ErrorService, private $stateParams: INewFallStateParams, wizardStepManager: WizardStepManager,
                private $translate: ITranslateService, private $q: IQService, $scope: IScope, private authServiceRS: AuthServiceRS) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.GESUCH_ERSTELLEN);
        this.readStateParams();
        this.initViewModel();
        this.TSRoleUtil = TSRoleUtil;
    }

    private readStateParams() {
        if (this.$stateParams.createNew === 'true') {
            this.createNewParam = true;
        }
        if (this.$stateParams.createMutation === 'true') {
            this.createMutation = true;
        }
        if (this.$stateParams.eingangsart) {
            this.eingangsart = this.$stateParams.eingangsart;
        }
        if (this.$stateParams.gesuchsperiodeId && this.$stateParams.gesuchsperiodeId !== '') {
            this.gesuchsperiodeId = this.$stateParams.gesuchsperiodeId;
        }
        if (this.$stateParams.fallId && this.$stateParams.fallId !== '') {
            this.fallId = this.$stateParams.fallId;
        }
    }

    public setShowError(showError: boolean): void {
        this.showError = showError;
    }

    private initViewModel(): void {
        if (this.gesuchsperiodeId === null || this.gesuchsperiodeId === undefined || this.gesuchsperiodeId === '') {
            if (this.gesuchModelManager.getGesuchsperiode()) {
                this.gesuchsperiodeId = this.gesuchModelManager.getGesuchsperiode().id;
            }
        }
        this.gesuchModelManager.initGesuchWithEingangsart(this.createNewParam, this.eingangsart, this.gesuchsperiodeId, this.fallId);
        if (!this.gesuchModelManager.getAllActiveGesuchsperioden() || this.gesuchModelManager.getAllActiveGesuchsperioden().length <= 0) {
            this.gesuchModelManager.updateActiveGesuchsperiodenList();
        }
    }

    save(): IPromise<TSGesuch> {
        this.showError = true;
        if (this.isGesuchValid()) {
            if (!this.form.$dirty && !this.gesuchModelManager.getGesuch().isNew()) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.gesuchModelManager.getGesuch());
            }
            this.errorService.clearAll();
            if (this.gesuchModelManager.getGesuch().typ === TSAntragTyp.MUTATION && this.gesuchModelManager.getGesuch().isNew()) {
                this.berechnungsManager.clear();
                return this.gesuchModelManager.saveMutation();
            } else {
                return this.gesuchModelManager.saveGesuchAndFall();
            }
        }
        return undefined;
    }

    public getGesuchModel(): TSGesuch {
        return this.gesuchModelManager.getGesuch();
    }

    public getAllActiveGesuchsperioden() {
        return this.gesuchModelManager.getAllActiveGesuchsperioden();
    }

    public setSelectedGesuchsperiode(): void {
        let gesuchsperiodeList = this.getAllActiveGesuchsperioden();
        for (let i: number = 0; i < gesuchsperiodeList.length; i++) {
            if (gesuchsperiodeList[i].id === this.gesuchsperiodeId) {
                this.getGesuchModel().gesuchsperiode = gesuchsperiodeList[i];
            }
        }
    }

    public getTitle(): string {
        if (this.gesuchModelManager.isErstgesuch()) {
            if (this.gesuchModelManager.isGesuchSaved() && this.gesuchModelManager.getGesuchsperiode()) {
                return this.$translate.instant('MENU_ERSTGESUCH_PERIODE', {
                    periode: this.gesuchModelManager.getGesuchsperiode().gesuchsperiodeString
                });
            } else {
                return this.$translate.instant('MENU_ERSTGESUCH');
            }
        } else {
            return this.$translate.instant('ART_DER_MUTATION');
        }
    }

    public getNextButtonText(): string {
        if (this.authServiceRS.isOneOfRoles(this.TSRoleUtil.getGesuchstellerOnlyRoles())) {
            return this.$translate.instant('WEITER_ONLY_UPPER');
        }
        return this.$translate.instant('WEITER_UPPER');
    }
}
