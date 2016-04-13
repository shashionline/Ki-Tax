import TSAdresse from '../../../models/TSAdresse';
import AdresseRS from '../../service/adresseRS.rest';
import TSLand from '../../../models/TSLand';
import ListResourceRS from '../../service/listResourceRS.rest';
import * as adrTempl from './dv-adresse.html';

export class AdresseComponentConfig implements angular.IComponentOptions {
    transclude = false;
    bindings: any = {
        adresse: '<',
        prefix: '@'
    };
    template = adrTempl;
    controller = DvAdresseController;
    controllerAs = 'vm';
    require: any = {parentForm: '?^form'};
}


export  class DvAdresseController {
    static $inject = ['AdresseRS', 'ListResourceRS'];

    adresse: TSAdresse;
    prefix: string;
    adresseRS: AdresseRS;
    parentForm: angular.IFormController;
    popup: any;   //todo team welchen datepicker wollen wir
    laenderList: TSLand[];

    /* @ngInject */
    constructor(adresseRS: AdresseRS, listResourceRS: ListResourceRS) {
        this.adresseRS = adresseRS;
        this.popup = {opened: false};
        listResourceRS.getLaenderList().then((laenderList: TSLand[]) => {
            this.laenderList = laenderList;
        });
    }

    submit() {
        this.adresseRS.create(this.adresse)
            .then((response: any) => {
                if (response.status === 201) {
                    this.resetForm();
                }
            });
    }

    createItem() {
        this.adresse = new TSAdresse('', '', '', '', '', undefined, '', undefined, undefined, undefined);
    }

    resetForm() {
        this.adresse = undefined;
    }

    openPopup() {     //todo team welchen datepicker wollen wir
        this.popup.opened = true;
        console.log(this.popup.opened);
    }

}

