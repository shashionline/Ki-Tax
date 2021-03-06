/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import {getFristverlaengerungAsMoment, getTSFristverlaengerungValuesForGS, TSFristverlaengerung} from '../../../models/enums/TSFristverlaengerung';
import TSGesuch from '../../../models/TSGesuch';
import GesuchRS from '../../service/gesuchRS.rest';
import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IPromise} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import TSDownloadFile from '../../../models/TSDownloadFile';
import {isAtLeastFreigegeben, TSAntragStatus} from '../../../models/enums/TSAntragStatus';
import DateUtil from '../../../utils/DateUtil';
import {ApplicationPropertyRS} from '../../../admin/service/applicationPropertyRS.rest';
import {FreigabeDialogController} from '../../dialog/FreigabeDialogController';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import EbeguUtil from '../../../utils/EbeguUtil';
import IScope = angular.IScope;
import ITimeoutService = angular.ITimeoutService;
import moment = require('moment');

let template = require('./freigabeView.html');
require('./freigabeView.less');
let dialogTemplate = require('../../dialog/removeDialogTemplate.html');

export class FreigabeViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = FreigabeViewController;
    controllerAs = 'vm';
}

export class FreigabeViewController extends AbstractGesuchViewController<any> {

    bestaetigungFreigabequittung: boolean = false;
    isFreigebenClicked: boolean = false;
    private showGesuchFreigebenSimulationButton: boolean = false;
    TSRoleUtil: any;
    fristverlaengerungValues: Array<any>;
    private fristverlaengerungEnumValue: TSFristverlaengerung;

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager',
        'DvDialog', 'DownloadRS', '$scope', 'ApplicationPropertyRS', 'AuthServiceRS', 'GesuchRS', '$timeout'];

    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager, private DvDialog: DvDialog,
                private downloadRS: DownloadRS, $scope: IScope, private applicationPropertyRS: ApplicationPropertyRS,
                private authServiceRS: AuthServiceRS, private gesuchRS: GesuchRS, $timeout: ITimeoutService) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.FREIGABE, $timeout);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.initDevModeParameter();
        this.TSRoleUtil = TSRoleUtil;
        this.setFristverlaengerungValues();
        if (this.gesuchModelManager.getGesuch().id) {
            this.gesuchModelManager.reloadGesuch().then((gesuch: TSGesuch) => {
                this.fristverlaengerungEnumValue = this.gesuchModelManager.getFristverlaengerungAsEnumValue(gesuch);
            });
        }
    }

    public gesuchEinreichen(): IPromise<void> {
        this.isFreigebenClicked = true;
        if (this.isGesuchValid() && this.bestaetigungFreigabequittung === true) {
            this.form.$setPristine();
            return this.DvDialog.showDialog(dialogTemplate, FreigabeDialogController, {
                parentController: this
            });
        }
        return undefined;
    }

    public confirmationCallback(): void {
        if (this.gesuchModelManager.isGesuch()) {
            this.openFreigabequittungPDF(true);
        } else {
            this.gesuchFreigeben(); //wenn keine freigabequittung noetig direkt freigeben
        }
    }

    public gesuchFreigeben(): void {
        let gesuchID = this.gesuchModelManager.getGesuch().id;
        this.gesuchModelManager.antragFreigeben(gesuchID, null, null);
    }

    private initDevModeParameter() {
        this.applicationPropertyRS.isDevMode().then((response: boolean) => {
            // Simulation nur fuer SuperAdmin freischalten
            let isSuperadmin: boolean = this.authServiceRS.isOneOfRoles(TSRoleUtil.getAdministratorRoles());
            // Die Simulation ist nur im Dev-Mode moeglich und nur, wenn das Gesuch im Status FREIGABEQUITTUNG ist
            this.showGesuchFreigebenSimulationButton = (response && this.isGesuchInStatus(TSAntragStatus.FREIGABEQUITTUNG) && isSuperadmin);
        });
    }

    public isGesuchFreigegeben(): boolean {
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().status) {
            return isAtLeastFreigegeben(this.gesuchModelManager.getGesuch().status)
                || (this.gesuchModelManager.getGesuch().status === TSAntragStatus.FREIGABEQUITTUNG);
        }
        return false;
    }

    public isFreigabequittungAusstehend(): boolean {
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().status) {
            return this.gesuchModelManager.getGesuch().status === TSAntragStatus.FREIGABEQUITTUNG;
        }
        return false;
    }

    public openFreigabequittungPDF(forceCreation: boolean): IPromise<void> {
        let win: Window = this.downloadRS.prepareDownloadWindow();
        return this.downloadRS.getFreigabequittungAccessTokenGeneratedDokument(this.gesuchModelManager.getGesuch().id, forceCreation)
            .then((downloadFile: TSDownloadFile) => {
                // wir laden das Gesuch neu, da die Erstellung des Dokumentes auch Aenderungen im Gesuch verursacht
                this.gesuchModelManager.openGesuch(this.gesuchModelManager.getGesuch().id)
                    .then(() => {
                        this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
                    })
                    .catch((ex) => {
                        win.close();
                    });
            });
    }

    public isThereAnySchulamtAngebot(): boolean {
        return this.gesuchModelManager.isThereAnySchulamtAngebot();
    }

    public getFreigabeDatum(): string {
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().freigabeDatum) {
            return DateUtil.momentToLocalDateFormat(this.gesuchModelManager.getGesuch().freigabeDatum, 'DD.MM.YYYY');
        }
        return '';
    }

    public getTextForFreigebenNotAllowed(): string {
        if (this.gesuchModelManager.getGesuch().gesperrtWegenBeschwerde) {
            return 'FREIGABEQUITTUNG_NOT_ALLOWED_BESCHWERDE_TEXT';
        } else if (this.gesuchModelManager.isGesuchsperiodeReadonly()) {
            return 'FREIGABEQUITTUNG_NOT_ALLOWED_GESUCHSPERIODE_TEXT';
        } else {
            return 'FREIGABEQUITTUNG_NOT_ALLOWED_TEXT';
        }
    }

    private setFristverlaengerungValues(): void {
        this.fristverlaengerungValues = getTSFristverlaengerungValuesForGS();
    }

    /**
     * Die Methodes wizardStepManager.areAllStepsOK() erlaubt dass die Betreuungen in Status PLATZBESTAETIGUNG sind
     * aber in diesem Fall duerfen diese nur OK sein, deswegen die Frage extra. Ausserdem darf es nur freigegebn werden
     * wenn es nicht in ReadOnly modus ist
     */
    public canBeFreigegeben(): boolean {
        return this.wizardStepManager.areAllStepsOK(this.gesuchModelManager.getGesuch()) &&
            this.wizardStepManager.isStepStatusOk(TSWizardStepName.BETREUUNG)
            && !this.isGesuchReadonly() && this.isGesuchInStatus(TSAntragStatus.IN_BEARBEITUNG_GS);
    }

    public canBeFristverlaengerung(): boolean {
        let fristverlaengerungerlaubt = moment(moment.now()).isBefore(getFristverlaengerungAsMoment(
            TSFristverlaengerung.FRISTVERLAENGERUNG_SEPTEMBER, this.gesuchModelManager.getYearOfGesuchsperiodeBegin()));
        return !this.isGesuchReadonly() && fristverlaengerungerlaubt;
    }

    public changeFristverlaengerung() {
        this.gesuchModelManager.getGesuch().fristverlaengerung =
            getFristverlaengerungAsMoment(this.fristverlaengerungEnumValue, this.gesuchModelManager.getYearOfGesuchsperiodeBegin());
        this.gesuchRS.changeFristverlaengerung(
            this.gesuchModelManager.getGesuch().id,
            this.gesuchModelManager.getGesuch().fristverlaengerung
        ).then(() => {
            this.gesuchModelManager.setGesuch(this.gesuchModelManager.getGesuch());
            this.form.$setPristine();
        });
    }

    public isGesuchstellerRole(): boolean {
        return this.authServiceRS.isOneOfRoles(TSRoleUtil.getGesuchstellerRoles());
    }

    public isThereAnyAbgewieseneBetreuung(): boolean {
        return this.gesuchModelManager.isThereAnyAbgewieseneBetreuung();
    }

    /**
     * Wir koennen auf jeden Fall sicher sein, dass alle Erstgesuche eine Freigabequittung haben.
     * Ausserdem nur die Mutationen bei denen alle JA-Angebote neu sind, werden eine Freigabequittung haben
     */
    public isThereFreigabequittung(): boolean {
        return this.gesuchModelManager.isGesuch();
    }

    $postLink() {
        this.$timeout(() => {
            EbeguUtil.selectFirst();
        }, 100);
    }
}
