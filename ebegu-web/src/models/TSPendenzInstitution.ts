import TSGesuchsperiode from './TSGesuchsperiode';
import {TSBetreuungsangebotTyp} from './enums/TSBetreuungsangebotTyp';
import TSInstitution from './TSInstitution';

export default class TSPendenzInstitution {

    private _betreuungsId: string;
    private _gesuchId: string;
    private _name: string;
    private _vorname: string;
    private _geburtsdatum: moment.Moment;
    private _typ: string;
    private _gesuchsperiode: TSGesuchsperiode;
    private _eingangsdatum: moment.Moment;
    private _betreuungsangebotTyp: TSBetreuungsangebotTyp;
    private _institution: TSInstitution;

    constructor(betreuungsId?: string, gesuchId?: string, name?: string, vorname?: string, geburtsdatum?: moment.Moment, typ?: string,
                gesuchsperiode?: TSGesuchsperiode, eingangsdatum?: moment.Moment,
                betreuungsangebotTyp?: TSBetreuungsangebotTyp, institution?: TSInstitution) {
        this._betreuungsId = betreuungsId;
        this._gesuchId = gesuchId;
        this._name = name;
        this._vorname = vorname;
        this._geburtsdatum = geburtsdatum;
        this._typ = typ;
        this._gesuchsperiode = gesuchsperiode;
        this._eingangsdatum = eingangsdatum;
        this._betreuungsangebotTyp = betreuungsangebotTyp;
        this._institution = institution;
    }

    get betreuungsId(): string {
        return this._betreuungsId;
    }

    set betreuungsId(value: string) {
        this._betreuungsId = value;
    }

    get gesuchId(): string {
        return this._gesuchId;
    }

    set gesuchId(value: string) {
        this._gesuchId = value;
    }

    get name(): string {
        return this._name;
    }

    set name(value: string) {
        this._name = value;
    }

    get vorname(): string {
        return this._vorname;
    }

    set vorname(value: string) {
        this._vorname = value;
    }

    get geburtsdatum(): moment.Moment {
        return this._geburtsdatum;
    }

    set geburtsdatum(value: moment.Moment) {
        this._geburtsdatum = value;
    }

    get typ(): string {
        return this._typ;
    }

    set typ(value: string) {
        this._typ = value;
    }

    get gesuchsperiode(): TSGesuchsperiode {
        return this._gesuchsperiode;
    }

    set gesuchsperiode(value: TSGesuchsperiode) {
        this._gesuchsperiode = value;
    }

    get eingangsdatum(): moment.Moment {
        return this._eingangsdatum;
    }

    set eingangsdatum(value: moment.Moment) {
        this._eingangsdatum = value;
    }

    get betreuungsangebotTyp(): TSBetreuungsangebotTyp {
        return this._betreuungsangebotTyp;
    }

    set betreuungsangebotTyp(value: TSBetreuungsangebotTyp) {
        this._betreuungsangebotTyp = value;
    }

    get institution(): TSInstitution {
        return this._institution;
    }

    set institution(value: TSInstitution) {
        this._institution = value;
    }
}