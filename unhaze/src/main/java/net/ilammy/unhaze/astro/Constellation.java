package net.ilammy.unhaze.astro;

public enum Constellation {
    Andromeda,
    Antlia,
    Apus ,
    Aquarius,
    Aquila,
    Ara,
    Aries,
    Auriga,
    Bootes,
    Caelum,
    Camelopardalis,
    Cancer,
    CanesVenatici,
    CanisMajor,
    CanisMinor,
    Capricornus,
    Carina,
    Cassiopeia,
    Centaurus,
    Cepheus,
    Cetus,
    Chamaeleon,
    Circinus,
    Columba,
    ComaBerenices,
    CoronaAustralis,
    CoronaBorealis,
    Corvus,
    Crater,
    Crux,
    Cygnus,
    Delphinus,
    Dorado,
    Draco,
    Equuleus,
    Eridanus,
    Fornax,
    Gemini,
    Grus,
    Hercules,
    Horologium,
    Hydra,
    Hydrus,
    Indus,
    Lacerta,
    Leo,
    LeoMinor,
    Lepus,
    Libra,
    Lupus,
    Lynx,
    Lyra,
    Mensa,
    Microscopium,
    Monoceros,
    Musca,
    Norma,
    Octans,
    Ophiuchus,
    Orion,
    Pavo,
    Pegasus,
    Perseus,
    Phoenix,
    Pictor,
    Pisces,
    PiscisAustrinus,
    Puppis,
    Pyxis,
    Reticulum,
    Sagitta,
    Sagittarius,
    Scorpius,
    Sculptor,
    Scutum,
    Serpens,
    Sextans,
    Taurus,
    Telescopium,
    Triangulum,
    TriangulumAustrale,
    Tucana,
    UrsaMajor,
    UrsaMinor,
    Vela,
    Virgo,
    Volans,
    Vulpecula;

    static Constellation fromIAU(String iauShortName) {
        switch (iauShortName) {
            case "And": return Constellation.Andromeda;
            case "Ant": return Constellation.Antlia;
            case "Aps": return Constellation.Apus ;
            case "Aql": return Constellation.Aquila;
            case "Aqr": return Constellation.Aquarius;
            case "Ara": return Constellation.Ara;
            case "Ari": return Constellation.Aries;
            case "Aur": return Constellation.Auriga;
            case "Boo": return Constellation.Bootes;
            case "Cae": return Constellation.Caelum;
            case "Cam": return Constellation.Camelopardalis;
            case "Cap": return Constellation.Capricornus;
            case "Car": return Constellation.Carina;
            case "Cas": return Constellation.Cassiopeia;
            case "Cen": return Constellation.Centaurus;
            case "Cep": return Constellation.Cepheus;
            case "Cet": return Constellation.Cetus;
            case "Cha": return Constellation.Chamaeleon;
            case "Cir": return Constellation.Circinus;
            case "CMa": return Constellation.CanisMajor;
            case "CMi": return Constellation.CanisMinor;
            case "Cnc": return Constellation.Cancer;
            case "Col": return Constellation.Columba;
            case "Com": return Constellation.ComaBerenices;
            case "CrA": return Constellation.CoronaAustralis;
            case "CrB": return Constellation.CoronaBorealis;
            case "Crt": return Constellation.Crater;
            case "Cru": return Constellation.Crux;
            case "Crv": return Constellation.Corvus;
            case "CVn": return Constellation.CanesVenatici;
            case "Cyg": return Constellation.Cygnus;
            case "Del": return Constellation.Delphinus;
            case "Dor": return Constellation.Dorado;
            case "Dra": return Constellation.Draco;
            case "Equ": return Constellation.Equuleus;
            case "Eri": return Constellation.Eridanus;
            case "For": return Constellation.Fornax;
            case "Gem": return Constellation.Gemini;
            case "Gru": return Constellation.Grus;
            case "Her": return Constellation.Hercules;
            case "Hor": return Constellation.Horologium;
            case "Hya": return Constellation.Hydra;
            case "Hyi": return Constellation.Hydrus;
            case "Ind": return Constellation.Indus;
            case "Lac": return Constellation.Lacerta;
            case "Leo": return Constellation.Leo;
            case "Lep": return Constellation.Lepus;
            case "Lib": return Constellation.Libra;
            case "LMi": return Constellation.LeoMinor;
            case "Lup": return Constellation.Lupus;
            case "Lyn": return Constellation.Lynx;
            case "Lyr": return Constellation.Lyra;
            case "Men": return Constellation.Mensa;
            case "Mic": return Constellation.Microscopium;
            case "Mon": return Constellation.Monoceros;
            case "Mus": return Constellation.Musca;
            case "Nor": return Constellation.Norma;
            case "Oct": return Constellation.Octans;
            case "Oph": return Constellation.Ophiuchus;
            case "Ori": return Constellation.Orion;
            case "Pav": return Constellation.Pavo;
            case "Peg": return Constellation.Pegasus;
            case "Per": return Constellation.Perseus;
            case "Phe": return Constellation.Phoenix;
            case "Pic": return Constellation.Pictor;
            case "PsA": return Constellation.PiscisAustrinus;
            case "Psc": return Constellation.Pisces;
            case "Pup": return Constellation.Puppis;
            case "Pyx": return Constellation.Pyxis;
            case "Ret": return Constellation.Reticulum;
            case "Scl": return Constellation.Sculptor;
            case "Sco": return Constellation.Scorpius;
            case "Sct": return Constellation.Scutum;
            case "Ser": return Constellation.Serpens;
            case "Sex": return Constellation.Sextans;
            case "Sge": return Constellation.Sagitta;
            case "Sgr": return Constellation.Sagittarius;
            case "Tau": return Constellation.Taurus;
            case "Tel": return Constellation.Telescopium;
            case "TrA": return Constellation.TriangulumAustrale;
            case "Tri": return Constellation.Triangulum;
            case "Tuc": return Constellation.Tucana;
            case "UMa": return Constellation.UrsaMajor;
            case "UMi": return Constellation.UrsaMinor;
            case "Vel": return Constellation.Vela;
            case "Vir": return Constellation.Virgo;
            case "Vol": return Constellation.Volans;
            case "Vul": return Constellation.Vulpecula;
            default:
                throw new RuntimeException("invalid IAU short constellation name: " + iauShortName);
        }
    }
}
