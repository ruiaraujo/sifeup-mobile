package pt.up.fe.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a Java Port of the passchk utility
 * available at http://rumkin.com/tools/password/passchk.php.
 * 
 * @author Rui Araújo & Ângela Igreja
 *
 */
public class PasswordCheck {
	public static final int VERY_WEAK = 1;
	public static final int WEAK = 2;
	public static final int AVERAGE = 3;
	public static final int STRONG = 4;
	public static final int VERY_STRONG = 5;
	public static final int COMMON_WORD = -1;
	public static final int TOO_SHORT = -2;
	
	//We know the sizes 
	final private List<String> CommonWords = new ArrayList<String>(7186);
	final private List<Double> FrequencyTable = new ArrayList<Double>(729);
	
	private String FrequencyList = "gL6-A$A:#@'$KT#<c\"06\"8*\"Wb\"5-$Kr t%#f+\"S4$7;$ v!R=%!6 g@#s}&'P#rM\"a8\"K'!3= #V }o wx*bZ\"X=\"De#2 #~B!dL!\\\\\"(u qj#Pf L_\"e (1K$9--x6 SE\"x- .$)/s%rK(w\"\"QV\"oQ ;; {_ [| q@(8t-G7#nU MW cS2$9 UJ GH <]*#S !: >!'kQ \\Z @A)O8 5& E (L\"\"YZ r_&g2 66 ~& *L!n* zO,|Y)B8 V.#X: qI'V0 B7 s@4<B)>= I-$wt!;A =h ?G+y: p/ |k\"}} &w\"]?#72 W) j$ @w S !I700V'gf (Q cf!9{9cL Qa .i #Z, F *M NZ!0c RX!+%((N S$ ]o#hU#{o Z2#j_ 6P d% I!!wX _Z.nH!i.!@y!6T#a$\"Yt ?]!kv Jl\"]{ sh!2$&x~\"5F.8X AF!n2 :A1G@('.%5} 24!TZ XY \\P zq G,).J)>} GD _o Xu*di&m0!%Q  L-KA @I ei%4P <S 8m+NI l& Gg&m+\"nS$T{%&? YT e( Mu g2 ~., B(\\P N6 .d .M:^6 gg\"]s\"k2':6 LU /h#k_ 6-#$3%p9 W$ 7x&)(%_-\">[#Q8 VM y( 3q c* vn/va-K> ^N Il LQ/y9 qF *< /k+^O zp K;\"bZ!M\\!>(*NW L' .-#=<\"Uz$^:$o? C] F' /)\"+) FC+8H$M&!l1&2'#n\"&K<!d-#\"_ jw A{ J(#~e$Yi#(v.Ng#NF!V8 zM\"Q4*RH&.X E\"\"n; ^5 41 gM!xj(\"Q1![ bn #!\";?1la yU mR A6)GK @=\"w5!k$!bX\"b[)K-!R) Mq Fl&uz Zx&p\\ <A X' f&!B_ xj)GW.3F <G *5 .e-}6 (6 C-!U>)S< :r#=;#:C 9Q!xJ,7< w\" Tp#*1%|F#Ze&JW Dn /$ ](\"4( H{&>!.@h W[ .F\"G}/#k :_ ,U ;$04E \\,!/P)h9!Hq f4( f S& 18 M^\":'#w|#]o 7= !# Y]\"*4 YZ-<E0$]\"B^ QO bn/$G )> zO e>-|+ \\? SY (d$nj b%)p+%aQ 2P oa!U\\ mp$x: S: *$ &.!9( 3-3KD&WW WG\"jB'`),:t I<'T_ I=($C Pd!;M 5I 3a\"h %_G $, .E (_%wZ(WC!*] w> \"$ s} WT h;*Kd UY!JW\")u\",)\"v6!AU\"Mv RS\"hZ \"(\"Ya&e/%5S-w\\\"/W#b1 lF*fp%a6$3c$aH#uf!e> cH!]v 58*En+r< Ic v< gh,&\\ +A ;a$2!)I. !8 :w$>E 2Q fH+LA$)$ Gl+NL\"y9\"43$30 \"8 d% 8l u% cV$pm 6C 2O uH .S K@ $= DB B(!f6 =9 <R *I uH A@ dG )A `K NX 3L !sopL %: o1 h0 9/ (U(5?-Q8!&=!iX\"f(0\\[ ~V!ii )\\,<? 2W!l*!}#\"\\8#NX)pn S0 Iv!{M$Rs#A2#s\"!$R k& ]J!Yi {#4jR%m` --$\\D ;(*>X ]K lS\"H~'c; ;K#hP!/^!(l !S#z+##d [H F\\%Z3/b-#)f /O c% 66!YO 2w+{3,}? {N I1  O3^6 !? xV\"s5-3I `R wn (E wj Kq';: KA XU&_'#b|$<N$2C qX w# iu!yO Fr($1\"T@\"pg\"eA#+3'g<!w$\":w `7$\\ !Z)#qf%I4%$x*pf!kN\"/* T9(Z])+?&O5\"9\"!nP 2< {9 5q R-'0~5)4 ^= j8 us9#/ @1 }3 Ul0+- U6 *K!&? +K!]p()0 Z$ va!e} Y< fl!rQ *) C( KJ#f4 $;'v.5aO W` w*!A<0IC yG ru!KR-*/ <\\ ,-!&3!Ap&Jc'+` V& s5\"gB$gF!`>!B0 hO g. ;w\"I5 |v7:@&:o %h#zz H\"(wx tL rk!q\\*w9 {D Q-!T_!lF!O$%vF&cB 8 \"Mp!SZ'he!HP rq!>N\"BA\"Y3 wR8^U$.~ ee#K[\";,#IZ ;f!KI!eE!XQ d-#s/$JY%Pz%/F$+Y#2$ XX#Ez'5g$z9\"9$!1y vB 51!La @T*\\f0)j ek L3!^@1t- II Zn m@,08 )0!-@!<-! |\"Fp&Oj A& N_ U[ P>!-h$D` 3a!K' `d#uc%";
	private String CommonList = "A A!@#$%F^G&H*A*A.,mDnEbA/.,DmEnFbBdev/nullBetc/passwdBusr/groupA000D0E0F0G0H0D7C7D007B213C46D9A1B022D9E3F8C6Csne1B11D1E1F1G1H1B209C12E12D3D4D7C25C3D098D123D321D4E5F6G7H8EqwerDabcDgoB313E13D6C32C579B412C30C430B701dC1717B812overtureD8E18B900D1D2D3D4D5D6D7D8D9C10D1D2D3D4D5D6D7D8D9C20D1D2D3D4D5D6D7D8D9C30D1D2D3D4D5D6D7D8D9C40D1D2D3D4D5D6D7D8D9C50D1D2D3D4D5D6D7D8D9C60D1D2D3D4D5D6D7D8D9C70D1D2D3D4D5D6D7D8D9C80D1D2D3D4D5D6D7D8D9C90D1D2D3D4D5D6D7D8D9Ba2b3cBchrisBkittyBp2o3iBq2w3eCw23eBsanjoseA20C00D1D2D3D4D5D6D7D8D9C10D2D3D4D5D6D7D8D9C20D1D2D3D4D5D6D7D8D9C30D1D2D3D4D5D6D7D8B112E2112B2C00C2D2E2F2G2H2C52BkidsBwelcomeA3B010B112C41B33D3E3F3G3H3B533B69BbearsA4B.2bsdC3bsdB055C77mashB2bsdB3bsdB44D4E4F44H4B788B854BrunnerA5B050B121B252B4321B55D5E5F5G5H5B683B7chevyBand5A6262B301B54321B66D6E6F6G6H6B969E69Czulu4zA777D7E7F7G7H7B89456BdwarfsA80486B675309B7654321B88D8E8F8G8H8A90210B11DscFturboDturboB2072B99D9E9F9G9H9A;lkD;lkDasdA@#$%^&AaB12345Cb2c3Gd4BaCaDaEaFaGaHaCrdvarkDonDtiBbacabDdabdooCbotFtDyCcD123DdE123H4EeFfGgCdenaceDolDulFkafFlahErChijitEramCigailCoutCracadabraIverEhamErCsolutBcaciaDdemiaHcCceptEssDordEuntHsCeCknakCropolisCtionEveDorCuraBdaDmEsCelCgCiDbDdasDneCmDinF1FistIratorCrianGnaHeEenGneDockCultCventurDilBehCneasCrobicsBfreshDicaEdCterBgainCentCgieFsCnesBhideeCmedEtBikmanCleenCmeeCrborneDcraftDheadDplaneDwolfBjaiDyBkhilCi123DkoBlainDmgirDnDsEkaEtairDyneCbanyEtrosIsDertGoCcaponeCejandrDnaDrtDssandDxE1EandeIrHrEendrEiaFsCfDaroDredCgebraCiDasFesDcaEeF1EiaDenFsDnaEeDsaEonClDahEnDegroEnDisonDoDstateCohaDkCphaF1FbetDineCtafEmiraDheaDimaG1CvaDinCwaysCysonEsaBmaDdeusDndaG1DrEjitEpreeDzingCberCelieDricaH7CiDgaCorphousDsDurCrilCyBnC-jenCacondaDlEogDntFhDstasiCchanaEorCdDersGonDiDreF1FaG1GsFwG!G1EoidFmacheGedEzejDyCelieseDwpassCgelF1FaG1FikaFsErineDieF1DusCiDlDmalG houseGhouseGsDsDtaCjanaDenCnDaElenaFiseEmariDeEliEtteDiEeConEymousCswerCtaresDhonyEropogenicDoineEnFioFyCuDmber1HoneDpaFmDragCvilsCyDthingBpacheClColloG13CpleF1F2FiiFpieFsCrilCtivaBquaEriousGusBragornDmDshCbenzCchieFtectDticCdentCeDleneCiDaEdneEneDelFlaDfDjitDndamDstotleDzonaCjunFasaCleneCmandGoDondCnoldConDundCrowCsenalDhadCtDemisDhurDieEstDyCunEaCvindBsCadDpCdDfE1234E;lkjEasdfEgFhGjHkEjklH;DlkjChimaEshDleyG1DokDrafDtonDutoshCianCjeetCkCmCpenCsDholeDmunchCterixBtC&tCandtCeChDanassDenaClantaCmosphereCseCtilaCulBudieDraEeyCgustGinCreliusCstinCthorDumnBvalonDtarCengerEirCiCniCrahamBwayE!CesomeByeletClmerBzamCizEiCtecsCureAbabakDeEsDiesDyEdollElon5CcchusDhDkdoorErubEupCdassDboyDgerDtimesCgladyDwomanChramCileyCkedpotatoeErDshiClakrisEsFubrDdoDkrishDlardEsCmbiEooCnDanaGsFeDcroftDditDgDksDzaiCrakaDbEaraEerEieDfEerEingDitoneDnEesFyG1EieEyardDonF harkonnenFharkonnenDretGtEyDtEmanEonCsebalHlDfEulDicElDkarEetGbHaIllDsEoonDtardDukiCtcaveEhEomputerDmanG1EobileCystateBballCbDbEbFbGbHbBeCachFesDgleDmmeupDnerEieEsDrEsDstFyDterEitElesEriceDutifuIlFyDverEisG1CbeCcauseDcaDkyCefDnDrDthovenCforeChnamClgiumDizeDlEaEeEowDmontDovedCnDgtDjaminEiDnetGtEyDoitDsonDtDyDzCowulfCppeCresforDhanuDkeleyDlinGerGwallDnardHoEhardEieDryDtEhaDylCstCtaEcamDhEanyDsieEyDterEieEyCverlyBfdCiBharatDvaniCoothapBiayCcameralDhngaEonCenveniCgDalDbenEirdEossFyErotherEucksDcockHsDdealEogEudeDfootDglesEuyDhipsEouseDjokeDmacFnEouthDredEoomDsecretDtitsEoeCkerClboDiameeDlEcEieEsEyF1CmDboFeDmerCnDdDgEoDkyDodCoboyDchemDlogyCrdE33EieEyDgetGtaEitDthdayCscuitDhopDmillahCtchFinH'HgDemeDterCzDhanBjornBlackFbootFieFjackDderunnerDhDineErDkeDncheDsterDzerCeepFingFsCindsDssDtzDzzardCondeFieFsG1DodEmcountyDwEfishEjobEmeEoffCssCueEbirdFlazerEeyesEfishEjeanElineEsFkyEvelvetBmwBoCatCbDbiFjoEyDcatCdyshopCeingCgartDeyDusCleslawCmbayCndE007DerDgDitaDjourDkersDnEieDsaiDzaiEoCobieGsEooEysDgerEieDkEemGdannoEitDmerDnDsterDtsFieDzieCrDisDnagaiCscoDsDtonCthCulderDrbonEneG-againGagainCwlingCxerFsCydDwonderCzoBradE&janetEfordEjanetEleyEnjanetDinFdeadDnchEd-n-janetFiFonFyEislaDsilDtDvenewworldFsDzilCeakoutEstGfeedGsDndaGnFenEtDtEonEtDwsterCianDcklesFoutDdgeGsGtHtDefcaseDghtDngEkleyDtainCoadwayDkenheartFrDmbergDncoGsEteDokeFsDthelGrHsDwnFsCuceDnoDtusCyanFtDceDnBsdD4DunixBubbaF1FhGlahFlahEleGsCckEarooEsCddEahEhaFistEyDgieDliteCffaloEettEyCgsEbunnyEyCllEdogEetEsFhitCmblingCngDnyFrabbitCrgessDkeDnsDtonCsalaccDinessDterCtDchDlerDtEerGflyEfuckIerEheadEonGsCyCzzByCoungGinCronCtemeCungAc++B00perB3poBabernetDinboyCctusCdDatDcamDweldCesarCipEcadDtlinClDebEndarDgaryDibanEfornIiaDlDvinG1CmaroEyDelEraFonDillaGeDlinDpanileEbellEingCnDadaDcedFrDdaceEiEyDelaDnonGdaIleDonDtorDucksEteCpfastDtainEianCrDbonDdEinalDebearEnEyDlEaEenaEoFsEyleFnDmenDnageDolF1FeGenFieGnaHeFynDrieEolFtEyDsonDterEmanDverDyElEnCscadeHsDeyDhEboxDioDparEerDsieDtleCtDalinaFogDch22DfishDherinIeEiEleenEyDnipDsDwomanCyugaBccDcEcFcGcHcBecilFeFiaFyCdicClesteDiaEcaEneDticsCmentCnterCrebusDuleanCsarFeDsnaBfiCjBgjBhadDiEnFsawDkkalaDllengIeDmeleonEpionFsDnEceEdFlerFraHmHsEelFquaEgFeGdGitGmeGthisFhoFkyuEnelFiEshinEtalDoE-yanEfengEsDpmanDrdonnayEgerEityElesFieH1FottIeEmingEonDsDtDuCeckinFovDdsadaDeseGcakeDifDlseaH1DmEistryDnEgDowF-toDralaEryEylDssEterH1DungDvyF1CiD-pangEshunEtaiEwangEyaoDaE-huaFlinFyinGuEraDcagoEkenEoDefsEnDhsingDldsplayEinDnE-wEaFcatEgF-enGliGmeEookEpanDpEperDquitaDshengCldrnDoeCoDcolatIeDlDongG-hDpEsticksDuEetteCrisF1G23FpenFsGyFtG1GiaInHeHnIaIeGmasGopIhJerGyDonosCuDckFyDen-chGtsDnE-linFsheFyuEgF-naGpiGyaFenFyenDongDrchEn-huBicDeroCgarCmarronCndelynFrEiEyF1DemaCrcuitDqueDrusCvicElBlaireDmbakeDncyDptonDrenceEisaGsaEkFsonDssFicFrooImDudeGlFiaCeanerFfightFroomEtEvageDoCiffFordEtonDntFonDpperDtEorisCockEloDsefriendDudDverCuelessDsterHsBoatamundiEimundiCbainDraCcacolaEkolaDkDoCdeEnameDyCeCffeeChenCkacolaDeEisitClbyDdEcutsEshoulderEwarDemanEtteDinDleenFgeFtteEinsDorFadoFsEurDt45EraneDumbiaCmandurDbinationDeEdienneEonEtDmanderEradesEunicationDpaqEtonEuserveFteHrDradeHsCnceptEordeDdoFmGsDfidenEusedDnectFrEieDradDsoleEpirituEultaHiDtentErolDvexCokEieGsFngDlEbeanEmanDperDterCpperCraElynDdeliaDeyDinnaGeDkyDleneDneliaHusEflakeDonaDradoDvetteDwinCsmicEoFsCugarGsDldDntryDplesDrierEtneyDscousCventryCwboyGsDsCxCyoteBrack1FerDigDppEsDshcourseDwfordCeateFionGveDditDosoteDscentDtinDwCicketDminalDstinaConusDssDwEleyCugDiseDsaderCystalBs-eeCc298D412Die-ciCeeChrcBthreepoDulhuBudaDdlesCervoCmCnninghamDtCongCpcakeCrmudgeonDrentDtEisCstomerEsupCtDdownDieFpieDlassByberFpunkCcloneCnthiaCranoDilAdaddyCebumDdalusDhyunDmonGicGsCggerG1CilyDnDsieEyCkotaCleDiborEtDlasDtryDuCmeDienDmitDogranEnDrongsCnDaDceFrDeDgermouseDhDielG1GleDnaEiEyDteCphneDperCqingCrinDk1EmanEstarDrellFnEinEowEylDthFvaderDweiEinDylEouchCshaCtDaE1EbaseEtrainDooCveDidF1FoviFsEsCwitDnCyDtekBdanielrodDyCdDdEdFdGdHdBe'anCadE-headEaheadEheadGdDnEnaDthFstarCbDasishDbieDorahDraCcemberDkerCdheadFdDiCeDdeeDpakEfreezeEseaFixFpaceEthroatDznutsCfDaultDenseDoeCkaiClanoDeteDiverDnazDoisDtaDugeCmeterDoEnFicFsCnaliDisFeDnisEyDverCpecheDtCquinCrekDluenDrekCsareeDertDignEreeDkjetEtopDmondDperateDtinyCtleffDroitCutschCvDadminDelopEnDiceElFinsideEneDonCwDayneDeyDydecimalCxterBgjBhanDrmaGraCirajBiabloDgEsDlE-inFupEinEupDmondHsDnEaEeEnFeDzCckEensEheadEtracyCdCegoDselDtEerCggerDitalH1ClbertDipDlweedCmDitrisDwitCnaDeshDnerCpakDlomacIyDperDstickDtaCrect1GorDkCscEbrakesEjockeyEoFveryDkDneyCxieDonBoCanCcDtorDumentCdgerGsCesCgDbertDcatcherDfightDgieEyCitEnowCllarGsEyDphinHsCmainDenicoDinicHkGqueFoCnD'tDaldDeDgEmingDkeyDnEaDtknowCobieDfusDgieDkieDmE2DnDrsCpeyCrabEiDcasDiEsEtDkDothyCsCubleDdouDgEieElasCwnEtownBr.dementoCaftDgonG1GflIyGsDwDxoDzenCeamFerFsGcapeDwCillpressDnkDppingDverDzztCnoCopE deadEdeadDughtCugnigDmEsCydenBuaneCckEieEsbreathFoupCdeDleyCkeE letoEletoClceCmbassCnbarDcanDdeeDeDgeonsDnCplicateCstinEyCtchFessBvlinsideBwainDneDyneCightBylanAeB-mailBachCgerDleF1FsCrlDthCsierDterGnEonDyEcomeEgoElayCtDmeDshitHandBckartClipseBdCdieCenCgarDesCinburghDthCmundCouardCuardGoCwardGsDinFaBe-csCcsCeDeEeFeGeHeCyoreBffieBggheadBiderdownCeioCghtCleenCnsteinCrikBkaDterinBladioDineDnorCectricDmentDnaEiDphantCiDasDna1DotDsabetEsaDzabetIhClaDenDieEotGtEsCmiraDoEotazDstreeCoiseCsieCvinEraEsCwoodDynBmailCeraldCilEeEioEyCmanuelDiEttCoryCpireDtyhandedGeadedBndaEhCemyDrgyCgageDineGerDlandCigmaCriqueCterFpriseDropyCzoDymeBrateaCenityChDardCicE1EaEhDkEaDnClingCnestGoDieF1DstCoticCsatzCtyuiopCvanBscortG1CfandiaCmondCpanolCtablishEteDelleDherBtCaoinG shrdluGshrdluCeeDrnityChanCoileBuccDlidCgeneCngDjiCropeBvaDnEsCeDlynDrafterEyCieBxavierCcaliberHuIrDelEptCploreHrDonentErtDressCtensionCxxtremeByalAfaceDultyCilDrviewEwayEyringDthClconDsestartCmilyG1CncyDgCrDahDetheewellDflungDgoneDhadDmerEingDoutDrellDsideDukCsihuddDtEbreakElaneCtanehDboyDcatCustCyeEzBearlessCbruaryCedbackEmeCliciaEksEpeExCnderDrisCreydooDgusGonDmatDrariEetEisBffDfEfFfGfHfBghBictionCdelFityCeldCgaroDleafCleEsystCnanceDdDiteDnConaCreEballFirdEmanEnzeEwalkDstCshE1EerGsFsEheadEieFngDtCtnessCveBlakesDmingoDndersDshCdCeaDmingDtchGerDursCightDpEperCoatDphouseDresEidaH1DwEerGpotGsDydCuffyDteCyDawayDboyDerFsDingGfuckGleapBoghornCnDgCoDbarFzDlEproofDtEbalHlCrDamDbiddenDdDearmEsightFtDkedtoungeDmatDrestDsytheDtuneDwardCsterCulplayDndEtainDrEierEwheelEyearsCxDtrotDyladyCzzieBramemakerDnceGsHcIoFineGsHcFoisEkFaFenfurterFieFlinFnfurterDtCeak1FbrothersDdEdieFyEericHkEricDeEbirdEdomEmanDnchG1GfriesDshbreadFmeatCidayDedricEndGsDghtenDscoDtzCodoDgE1EgieHsFyEsDmDnt242FierDshmeatEtyBtpBubarCckE-offFyouEaduckEedFmFrEfaceEingFtElegEmeEoffEuEyouCgaziCllCnDctionDgibleEuyDkyDnyDtimeCrballCtureCzbatDzEballGtAgabbyDrielHlDyCdDiCelicCgeCilClaEgaExianFyDenDileoCmalDbitElerDesDmaphiCnapathDdalfDjaCoyuanCrciaDdenEnerDfieldEunkelDgoyleDlicDnetDpDrEettEyDthDyCshDmanDtonCtewayH2DorF1DtCussDtamCveEnDrielBedankenCminiCneEralEsisDiusCofEfFreyDrgFeG1FiaGnaCraldErdGoDdDgoryDmanGyH1DonimoDryDtErudeCtD fuckedElaidFostEstuffedDfuckedDlaidEostH!EuckyDoutDstuffedBgeorgeCgDgEgFgGgHgBhandiColamalDstBiancarlEtsCbbonsDsonCffyCgiClDbertDgameshDlesDmanCnaDgerGsDoCovanneCridharDlEsCselleCuseppeCveCzmoBlacierDdysCenEdaEeaglesEnCider1CobalDriaBmoneyBnuDemacsDsBoC awayH!Dfuck yourselfDjump in a lakeDto hellCaheadDlieDtDwayG!CblinEueCcougsCdDfleshDivaDzillaCesDtheCfishDoritDuckyourselfChomeCingCjumpinalakeCldEenEfingerGshEieDfEerEingDlumCneDorrheaDzalesHzEoCoberDdE-luckEafternoonEeveningEfightEgriefEjobEluckEmorningEtimesEwifeDfusEyDnightDseCpalonDherDinathCrdanEonDgeousFsDogCslingDonDtraightCtDoE hellEhellCugeDldCwestBraceDemeDhamEmDilDmpsDndmaEtDphicHsDtefulIdeadDvisDyEmailCeasyspoonEtDedEnFdayFlineEtingDgE1EgEoryDmlinHsDtaFlEchenEeFlEzkyCiffeyFinDpeDssomDzzlyCoovyDupDverDwCumpyCyphonBsiteBucciCenterDssEtCidoDllermDnnessDtarG1ClukotaCmbyDptionCnnerDtisCozhongCpiCrjotDuCsDtavoCyBwenBymnastAh2opoloBaCckEedFrCdCfidhDtanCggisChaCiDboDleyDrbagGllEilCkanClD9000DlEelujahEoFweenHllDtCmidEltonDletEinDmerGedEondDptonDsterCn-gyooDdilyEwaveHingDkDnaFhDsEelEoloFnEpeteCoCppeningEyF1G23FdayFendingCrdE2seeEcoreEdiskEiFsonEwareDkaraEonnenDlanEeyG1EotsDmonyDoEldDrietFsGonEoldEyDueEoDvardEeyCsDokDsanCttonCuhuaCveDivahCwaiiDkEeyeH1CyesCzelBeC'sdeadIjimCalthG1DnDrtFbreakFsDtEherH1H2DvenCbridesCctorCdgehogCeDralalDsungCidiDkeEkiDnleinErichEzClenFaFeDgeDlEoF1G23F8FhelloDpE123EerEmeCmantCndersonErixDningDryCrDbEertDeDmanEesDnandezDpesDsheyDveDzogCsdeadHjimCungCwlettCydudeDthereBhhDhEhFhGhHhBiawathaCberniaCddenCghlandFifeClarieDbertDdaDlEaryEelCmCroguchEkiEoEshiEyukiCsDtoireFryCtchcockDhereDlerBoaDngCbbesEitCckeyG1DusF pocusF-pocusFpocusCkClaDdDeDidayDlyDyE grailEgrailEshitCmayoumDeEbrewErFjEworkCnDdaF1DeyDgEkongEphucEtaoDkeyCodlumDkerGsDpsDsierDtersH2011EieCpeDscotchCrizonDnetGsEyDrorDseFsDusCsannaHhDeheadDtCtDdogDlipsDrodDtipCucineDseFwifeEtonCwDardG93DellDieBplabBsinDuwenCpiceBuangDshengCbbaFhubbaDertCdsonCeyCghEesDoDuesCiyingCmmerCndtDgEmokDterEingCongCrtCsbandDkersEiesDtlerCtchinsCuCyenCzurBwansooBydrogenFxylCeCmanCoDnDungCukAiB'mokFayBabgCnBb6ub9CanezCeleiveEieveCmDpcFatFxtDsuxCrahimBcapCecreamDmanConBdenticalCiotContknowBfC6was9CorgetFotBgnacioEtiusCuanaBhackedDoDteyouCtfpBiiDiEiFiGiHiBkonasCuoBlanCmariCoveuFyouCyaBmageEineCbroglioCinCokEayCpactElaDerialCslBnCcludeCderpalDianGaEgoEraDonesiaDraCfoErmixCgemarDmarDoDresGsEidDvarCheritthewindCigoCnaDocentFuousCsaneDertionDideEghtDtEallEructCtegraHlElErcourseFleafFnGetFracialDoDrepidCvinoveritasEsibleEteCxsBoanaCmegaCngBraCelandDneFeCfanCinaDsEhFmanClandeCmaDeliConmanCulianCvingBsCaDacDbelGleDjokeCelChmaelCiDdoreDlDsClandCmailCraelDealCsamCtoBtC'sajokeEokGayCaliaEyCsDajokeDokFayDy-bitsyEbitsyCty-bittyEbittyBuytrewqBvanCyBzzyAj0kerB1l2t3BackEieG1EolanternEsonDobDquelineGsCdeCeDgerDjinCggerDuarChanshiCiDkEneEumarDmeDnCkeEyDovCmaicaDesF1FbondDieElahEsonDjamCnDaEkiDeEkElEtDiceEeDnEaEyDuaryDvierDyCpanFeseDonCredCshoEvantDminGeDonF1DpalEerCtinCvedDierCwsCyDantaGhDneDsonCzzBeanE-baptisteFclaudeFfrancoisFmichelFpierreFyvesEandaEclaudeEetteEfrancoisEineEmichelEneFieEpierreEyvesCdDiCepcjG7EsterCfDfEeryEreyH1ChanClloEystoneCnDiferDkinsDnEiFeFferEyF1DsEenCrDaldDemyDomeDricFmyEyDseyCsseF1EicaFeDterDusF1FchristCthroGhGtullDta1CudiCwelsBiCachenDnEliEnEpingEwenCeChongCkunCllCmDboFbDiDminEyCnDgDshengCongCseongCtendraCxianBjjDjEjFjGjHjBkl123D;CmBnyeBoanEieEnFaFeDquimCcelynCdyCeDlEleDnaDrgDyChanFnGaH1DnE316ElennonEnyEsonCinE for freeEforfreeCjiDoCkerF1CleCnDathanDellEsDgE-iEguDiDnyCrdanG23EieDeanDgeCseEeEphDhEuaDiahEeCurEneyCyDceBsbachBuanCbileeCdasDiEantoEcaelEthDyCggleChaniCiD-fenDcyDlletDnClayneDesDiEaF2FnGaGnEeF1FnGneFtGteDyCmanjiDboDeauxDpE in a lakeEinalakeCnDeEbugDgleDiEorEperCpingEterCssiDtEdoitEeEfortheEiceH4FnG1GeCttaBvncByhAkacyCdoshCiCkaDogawaCl007DamazoEppaDiDyanEnCmCnDgEarooCosCraEleeDenF1DieEnFaFeDlDmaDyEnCseyDhtanCtDeErinaDherinIeEiEleenEreenFineFynEyDiEeF1EnaDrinaDsufumDydidCvehCyDlaEenBcinBeciaCeDpEerEoutDsChCithF1CllerFyEyF1DseyCnDdallDjiDnedyFthEyDobiDtEonDzoCralaDberosDiDmitDnelDriFeEyFaCshavDterCtanDchupCvinF1CwlCyDboardDpadBhanEhDyrollCoanhDiDngDsrowCuehF-hoDrsheeBianEgEuschDtCdderDsCeuCllerEmeDroyCmDberlyDmoDonCndEerDgEandiEdomEfishElearEsDsonCpCranDkElandDstenCssEa2EmeCtkatDtenG12GsEyFcatCwiBjhgfdsaBkkDkEkFkGkHkBlausCeenexCingonHsBnickersFsDghtGsCowCuteBoalaCichiCjiCkakolaDoCmbatCngjooDradCokCrdaBraigDmerCisEhnaHmEtaFenFiGeGnHaHeFyCystalFynaBunCoCrtBwanEgCokDngByahnCeongsoCleCraAlab1DtecCcrosseCddieDiesDleDyEbugCgerCidCkeErsDotaDshmanClitFhGaCmDbdaEertDerDinationCnDaDceFrDdryCpinCraDissaDkinDryF1DsonCserFjetDsie1DtEangoEtangoCtenightCughDraFeFmaeEelFnGceGtGzEieFndaEyCwDrenceDsonDyerCzareFusBeaDderDfDhDnnFeCbesgueDlancCd-zeppelinDdzeppelinDzepGpHelinCeCgalDendErChi3b15CisonEureClandCmonCnDaDnonDoreCoDnEardEceEidCroyCsDbianDlieDpaulDtatEerCtDiciaDliveDmeinDoDsgoDterGsCvCwisCxus1CzBiCbDertyDraFryCckEerDorneCenDwCfeCghtFsCkeCllianEyDyCmaDitedCnDcEolnDdaEsayFeyEyDgDhConEelEkingEsCsaDeDpDsabonDtCtterboxEleGhouseGshitIopGtoeCveEandletliveEnletliveErpooIlEsDiaEngCwanaCzDaErdDzyBjfCiljanaBkjDasdDhEgFfGdsDlkjBlewellyClDlElFlGlHlCoydBmnopBochDkEoutCgDanDgerDicalEnDosEutCisElaneCkeDiClaDitaDopcCndonDelyEstarDgEcockEerEhairFornErestEtoungeCokDneyDseEingCpezCrenFzoEttaDiEeEnDnaDraineEieDyCserDtCtfiDusF123CuDieEsFaFeDnetteDrdesCveElyEmeErFboyFsEyouCwgradeDlifeBpCadminBsdBtteBuanaCcDasDiaEeFnEferElleDkyF1G4FbreakFladyDyCigiDsDzCkeCluCmiereCnarlanderDdiDeDgCongCtherBydiaEeCleCndonDetteDnEeAmB1911a1BaartenCcDhaEineDintosIhDkDrossDse30EymaCdDboyDdieEockFgDeEleineGneFineDhuFsudDisonDmanFxDokaEnnaDyCgdalenDgieEotDicF1EqueDnumChbubaDeshDlonDmoudCiDaDdenDlEerEinglistEmanDneEsailEtCjorFdomIoCkeEbreadEdrugsEitGsoEloveEmeFydayEpeaceEwarDingitGloveDotoClcolmFmDibuDlardCnDagemeGrEhilDbatDchesterDdyDfredDgeshEueDiEshDoharEjEnDsetmanisEonDtraDuelDyCplesyrupCraEthonDcEelGlaHeHinEhEiFaFoEoEusEyDdiDekDgalitFretGidHtFuxEeFauxEieEoEueriteDiaF1FhG1FnGneEeF-madeleineFlleFttaHeElynEnaFeGrFoEoFnEposaEtialEusDjoryDkE1EetEoEusDlboroEenaGeFyDniDriageEucciDsEhalHlDtEhaFeEiFalFnG1GeHzGienHqEyDvinDyEamFnnEjaneDzecCsahiroDeDh4077DoudDsEcompDterG1GsDuhiroCthE-csEildeDildaDrixDtEherGwFiasGeuEi1FnglyCudeDiDreenEiceGioEoCvericHkCxDimeEneDmaxDwellHsmartCyDdayCzda1DinBeCaganDtEcleaverEloafEwagonCchEanicCdardDiaEcalCekieCgaEdethEnDgieCisterClDaineEnieDinaFdaEsaFsaDlaEonDodyDtinCmberGshipDoryDphisCnDdelDsuckCowCrcedesFrErediEureGyDdeDesDlinEotDmaidDrellEillEychristmasCtalFlicDroDsCxicoBgrBiamiCchaelH.H1FlEelG1GeGlHeEiganEouEyDkelFyG1EyDroFsoftCdnightDoriDvaleDwayCghtDuelChailDranCkaelDeE1EyDiDkoClanoDdredDesDindDkDlardEeniumFrEicenFeFonDoDtonCmiCndyDeEdErvaDgEheDhDimumDnieDotEuDskyDyeCracleEgeEndaDiamDrorCsanthropeDhaEkaDogynistDsionFrliEyDtyCtDchFellDtensBmmDmEmFmGmHmCouseBnbDvEcFxGzBobileDydickCdelsEmEsteDulaCgensDulFsChamedFmadGedEnCisesEheCjaDoCllyF1DsonGgoldenCmCndayDetEyF1DiEcaEkaEqueEtorDkeyG1DopolyDroeDsterDtEanaH3EhErealFoseEyCocowDkieDmooDnEbeamEpieDreEhtyDseFheaIdCparCraDeEcatsDganDleyDoniDpheusDrisDtEimerEsCseDheCtherDorFolaEwnCuDntainDseF1FmatEumiCviesCwgliCzartBr.DrogerCcharlieCgoodbarCwonderfulBt.xinuCichellCxinuBuad-dibEdibDmadinCchCffinChDammadCkeshDundClderG1CnaishDchkinDdeepCrphyDrayCscleDicFboxEmDtangH1CtantByCcroftxxxHyyyCpasswdHordCraDonDtleCselfDmutCungF-yuAnabilCdegeErDiaEneCftalyCgelCissanceCkamichiCliniCnDcyDetteComiDtoCpoleonCrcisoGseDendraCsaDcarDtyCtDachaEliaGeErajaEshaDhalieFnGaeGieDionGalIeEviteCuticaCveenEtteBcarCc1701HdHeCrBe1410sE69Ea69CalDrmissCbraskaCckrubCdCenieCilCkoCllieDsonCmesisCnaCpentheIsDtuneCrmalCsbitGtDsDtleEorCtDlinksDmgrDscapeDwareEorkHsCutrinoCvadaDerDilleCwDaccountDbloodDcourtDkidGsDlifeDpassDsDtonDuserHsDworldDyorkH1CxtDus6BghiCocCuyenBicaraoDholasGeDkElausDolasFeCelCgelDgerDhtmareFshadowFwalGindChaomaCkeDhilDiEtaDkiDolaosClsonCmhDrodCnaDersDoEnDtendoCrvanaH1CssanEeCtaDeBnnDnEnFnGnHnBoCamCbodyDuhikoEkoCelCfunCkiaClanCmoreCndetDeE1CpassCraDbertDeenEneDikoDmaFlFnDthwestEonCsecretDhirCtDebookEsDgayDhingDreFspassDta1DusedCuveauCvacancyDellEmberGreCwDayCxiousBroffBssBuclearCggetCkeEmCllCmberG1G9GoneGsCrseEieCtDmegDritionCucpByquistAoaCtmealCxacaBbiD kenobiEwan kenobiD-wanDwanCsessionBceanFographyDlotCtaviaDoberFreBdetteCileEonBfCfDiceBhshitCwellBicu812ClCvindBjrindBldDladyDpussyCinDveFrFttiEiaFerClieCsenBmeadDgaBnCceCeCionringsClineDyCstadBooDoEoFoGoHoCpsBpenEbarEdesktopFoorEsaysmeFesameEupDrEaFtorCusBrCacleDngeGlineGsCcaDhidCegonDoCgasmCionClandoCvilleCwellBscarCirisCullivaCwaldBtharDerCterDoBu812CrCssamaCtDlawDtolunchBverEkillEthrowFimeBwenCnDsBxfordBzzieDyApaagalCcersDificGqueDkardEerGsEratCdDaaaDdyDmaDoueCgeCigeDnlessEtFerCkistanCladinDlabDmerDomaCmDelaDpersCncakeDdaEoraDicDteraEherEiesCpaDerFsDiersDpasCquesCradigmEllelEnoiaEskevDfaitDisDkEerEinsDolaDrotDtEnerEonDvizCscalDsEionEwdForHdIlookhereCtDchesDelErneDriceGiaGkFotsDsyDtersonEiEonEyCulEaEeEinGeCvelCwanCymanEentDtonBcatCxtBeaceEhFesDnutGbutterGsDrlFjamCbblesCcheFurHsCdroF1CeblesDweeCgasusDgyChCkkaClagieCncilDelopeDguinDisDnyDtecoteEiumEtiCopleDriaCpperDsiCrakaDcolateEyDesEzDfectEormaDryDsimmonEonGaDvertCteErF1FkFpanFsonEyDuniaCugeotDrBgonderinBhamDntomCialphaDlEipGpeGsElipHsDshFyCoenixH1DneDtoCrackDeakDickCyllisBianoF1FmanFsCcardEssoDkEleDtureCerceEreDterCgeonDletCmpCnDgDkEfloyIdConeerDtrCpelineEorganEr1CrateDieCscesCtCzzaBlaintruthDneFtDtoDyEboyEerGsEgroundCeaseCierCoverCughDmbrandyDsDtoFnCymouthBmcBocusCeticEryChCirEeDssonHsDuEyFtGreClarFbearFisDeDiceEticsDlyDoDynomialCmmeCnderingDtiacCohEbearDkeyEieG1CpcornDeEyeDpyCrcDkEyDnEbayEmanEoFgraphyDscheH9I11J4DterElandEnoyCstelFrCwellErFtoolBppDpEpFpGpHpBrabhakaFuEirDdeepDiseDnabDsadEhantDtapEtDvinDyerCeciousDdatorDludeDmierDsenceGtEidentEtoGnDttyGfaceDvisionCiceDmusDnceGssGtonEtFempsGrFingDscaDvEateEsCoducersDfE.EessorEileDgramDmetheIusDnghornDpertyDsperDtectFlEozoaDviderCudenceBsalmsCychoBubDlicDusCckettCddinCllDsarCmkinpieDpkinCneetDkinCpDpetEiesEyF123CrnenduDpleCssyF1CtByramidDoCthonAq1w2e3BedBianCnsongBqqD111DqEqFqGqHqBualityCebecDenFieDntinDstCocBwaszxCerEtFyG12GuHiAr0gerB2d2BabbitG1CcerFxDhelGleEmaninoffDingDoonCdarDhaDioCfaelDfiDikiCghavGanEuDunathCidEerGsHofthelostarkDmundDnEbowEdropDssaEtlinCjDaEdasaDeebFvEndraDivCkeshCleighDphCmDachanEnaFiEraoDboF1DeauxEshDirezDonCnDcidDdalGlEolphFmEyF1DgerGsDjanCoulCpDtorCquelCscalDtaF1FfarianFmanCtDioCvenFsDiCyDmonaGdBeadEerEingDganDlEfriendEityElyEthingFimeCbeccaElsDootCdDbaronErickDcloudDdogDfishDlineDmanDrumDskinHsDwingEoodCebokDdDferCgDgaeEieDinaGldEonalEsCineCliantCmemberDiDoteDyCnaudFltDeEeEgadeDgarajCplicantDomanEnseDtileDublicCquestEinCscueDearchFuCtardCvolutionCxCynoldsCzaDnorBfsBhettCinoCjrjlbkConaEdaBiacsCbsCcardoH1DcardoDhEardH1HsIonEmondDkEiEyCddleDeCff-raffErafHfDrafGfCghtCleyCngoCpperEleCscCtDaCverFaDiBjeBoadE warriorErunnerEwarriorCbDbieEyDertG1GaGoGsDinFhooIdFsonDleyDocopEtFechFicsDynCcheFlleFsterDkEetG1EieEnrollEonEyF horrorF1FhorrorCdDentEoDgerDmanDneyDolpheDrigueIzCgerF1FsChitCiCknyClandGeDexDidexDlinCmDainEnFoEricDeoDmelDualdElanHsDyCnDakEldDenDiEnEttCokieDsterDtEbeerCpingCsaElieDeEbudElineEmaryEsDieEneDsEignoCthCugeEhDletteDndDte66CxanaDyCyDalFsBrrDrErFrGrHrBsmBtiCwoEdtwoBubenDyCdolfDyCeyCfusCgbyDgerEieriCknetClesCnDnerEingCoxinCshDsEelGlDtyCthEieElessCxCyDeByanCoheiDtaAsaabE900H0EturboCbbathDinaFeDrinaCcreCdeDieCfaaDetyG1DwatCgittaireCiDdDfallaDgonDkumarDlingEorDntFeCktiDuraClD9000DahEsanaDesDleEyDmonDomeEneDutCmDadamsEnthaDediDiamErDmieEyDpathEleGrEsonDsamEonDtaneyDuelEraiCnchezDdersHonEgorgEiEraFineEsmmxEyDfranHciscoDgEbangEoDhDiDjayEeevEoseH1DtaEiagoFsukEoCphireDphireCraEhF1DojCshaEiDkiaDsyCtoriDurdayFnG5GeGinCulDvignonCvageDeCwedoffCxonCyBbdcBcamperDrecrowEletHtChemeDnappsDoolDroedeCienceDubbaCoobyGdooEterH1DrpioHnDtEchEtF1FieFyDutFsCreamDofulaEogeDuffyCubaF1DmbagBdfghjklBeaDbreezeDnDrchDttleCbastienCchangDretG3DurityCeDkerDmeChoCiDgneurDveCkharClftimeCmperfiCnditDiorDsorConghooDulCptembeIrHreCquentCrenaFityDgeFiFyDverEiceHsCsameGstreetCthDupCungFhyuFkuCvakDenF7ErinCxDfiendDxxmeDyEteenCymourBhadowG1GsEysideDeDggyDhrokhDkespeareDllEomDmitaDnEaEghaiEnanFonFyEtanuFiDolinDradEcEiFynEkFsEleneEonEraDshankFiEtaDunDvedFnDwEnDyneDzamEzamCeDbaDelaEnaDffieldDilaDlEbyEdonEiaElFeyFyEterDnEgFluDpherdDrifEriGeFyEylCiDahnDdanDgenarFoDhEmingDmonDnEobuDpDrinElFeyDtE-headEfacedForbrainsEheadDueDvaFpraEersDzoomCleeDomoCoesDgunDlomDmitaDoterDrtyDtgunDutDwEerEoffCrdluDeeramCuDangDhuiDnDtdownEtleCyngBidDartaDekickDhartaDneyDoineCemensDrraCgmachiDnalFtureCllywalkDverGeEiaCmbaF1DmonsDonDpleFyEsonHsDsimCnaEtraDgEerEleCobahnCriEusCsterCtDeCupingCvakumaCxDtynineCzenineBkateFrCeeterCibumDdooDingDnnyDpEperH1FyCullDnkCydiveDlerDwalkerBlackerDyerCeazyDepFyCickDderDmeballDnkyDpCusDtBmallFcockFhipsFtalkGipsDshedFingCegmaCileF1FsFyDthFsEtyCokeFdhamFyDochDtherCurfyDtBnafooEuDkeFsDppelGrFleDtchCeezyDllCickerHsDperCoopFdogFyDrkydorkyDwEbalHlEflakeEingEmanEskiCuffyBoCapCber1CccerG1EorDrateHsCdD offDoffCftEballClangeDeilDomanFonCmanEsamaDbreroDeEbodyCnDdraDgmiaoEnianDiaEcFsDjaDnyDyEaConEmanDwonCphiaFeEomoreCrelDoorCssinaCtirisCuaDmitraDndDrceEireFsEmilkDvenirBpaceFmanDinDmDnishEkyDrksFyErowHsEtanDzzCecialEterFreDechEdFoFyDnceGrChDynxCiceDderGmanDffFyDkeF1DritGuHsanctuEoFsDtEfireClifFfCockDngeDokyElerEnDrtsDtCrangDingGerEteDocketCudDnkyDrsCyrogyraFsBquashDiresFtBridharDmatDnivasBssDsEsFsGsHsCuBtaceyEiFeEyDinlessDlkerDmosDnEislasEleyFyEtonDrE warsE69EbuckEgateElightEsFhipEtFerFrekEwarsDtEesEionEusCealthDelFeGrsDfanGoDllaDmpleDphF1FaneHiIeHyFenFiFonDrlingEn93DveF1FnG1GsFrDwartCickshiftDffdrinkFprickDmpyEulateDngF1FrayEkyDversCocksDneDpDrageEemEmFyCrangeHrGleEtFfordFoGcasterEwberIryDetchDiderDongCtngCuDartDdEentH2EfuckEioElyDffedHturkeyDmpyDpidDttgartBuCbgeniusDhasEdailEednuDodhDscriberDwayCccesGsDkEerEmeErocksEsCdeshnaDhakarEirDirCeDsecCgarFbearDihCkumarCltanDuCmmerEitDuinenCnD-spotDbirdDdanceFyDfireEloweIrDgDilDnyF1FvaleDriseDsetEhinHeDtoolsDweiCperFflyFmanFstageIrFuserFvisorDportHedDraCranetDeshDfEerEingCsanF1FnaGeDhaEilaDieCttonCvenduDroCzannaGeDieDukiDyBvenDrigeBwampratDneEsonCearerEtshopDdenDetieFnesFpeaFsFyCimEmerFingDngsetDtzerCooshDrdfishByamCbaseDilCdneyClvainEereFsteHreEiaFeCmbolDmetryDultCphilisGlisCsD5DadmGinDdiagHsDlibDmaintFnEgrDopDtemG5GfiveGvFstDvAt-boneBabDathaCcobellCdahiroDlockCffyCiDwanCjenCkDaEjiEshiDeE5EfiveEiteasyDujiClonCmDalEraEsDiEeDmieEyDtamCndyDgerineEoEuyDiaDjuDkerDnerDyaCoCpaniEsDeCrDaDdisDgasEetDheelDragonDzanCshaCtaDianaDsuoDtooDumCureauEusCyfurDlorCzdevilDmanGiaBbirdBchenCp-ipD/ipDipBeacherHsDkettleDpartyCchEnicalFoCdDdiEyF1FbearCeDnEagerEeyEfanEyCflonClecastFomEphoneDlDnetCmpEoralEtationFressCnDnisDtationCquilaCresaDiDminalDreEiFllEyF1CstE1F23E2E3EcaseEerEguyEiFngEtestEuserCtrisDsuoCxDasBgifBhaddeusDilandIeDnEasisEhEkFgodFyouDtEcherDvyCeDatreDbeefEirdsEossEutlerDcleDendDgreatescapeDirDjudgeDkingHandiDloraxDmEanEonkeyDnDodoraHeEphileDpenguinEroducersDreFalthingFsaGeEiddlerEonDseDyCiamDbaultGtDckcockFheadFskinDerryDlakaDnkEthighsDsEisitCoiDmasEpsonEsonDrneEstenDseCrasherDeeCuDmperDnderHbIallIirdHdomeDrsdayDyCx1138BianCffanyCgerF2FsDgerDhtassFcuntFendFfitDreCjunCkaCllCmDberDeEzoneDothyCnaDgDkerGbellDmanDtinCreswingCtanicDsCwCzianoBjahjadiBntBoCbiasDyCdDayDdCgetherDgleChruCkyoClkeinEienCmDateFoDcatDmyCneDiDyCoDlDsillyDtsieCpcatDgunDherDographyCrDcDnadoDontoDresDstenDtoiseEueCshiakiFbaFterDnowCtalDhedarkDo1EtoCucanDficDrDssaintCveCxicCyDotaDsrusBraciFeEtorEyDilblazerFerFsEningDnEsexualFferGigurationFitFmitFportDpdoorEperDshFcanDvailEelEisCeDasureDborDeEsDkDntDvorCiDalDbbleHsDciaEkyDdentDeuDnaDshFaEtanDtonDvialDxieCoffDjanDmboneDnDphyDubleEtDyCucEkFerFsDefriendEloveDmanEpetDstno1CyDaBseCingF taoF-taoFtaoCungDtomuBttDtEtFtGtHtCyBuanCbaEsCckerDsonCesdayClaDlCnasaladGndwichComasCrboF2DnerEleftErightDtleCttleCyenBwat123CeetheartFyDnexCilaDnsCoBylerF1BziDlaCuwangAudayCoBhnD-soonDsoonBliCricFhCtimateBmeshBndeadErgradJuateCguessableChappyCicornFsDformEyDgrafixDqueDtedEyDxE-to-unixHunixEmanEsuckGxCknownDownCtungBpCchuckConCperclassCsilonDtillCtohereCyoursBranusCbainCchinCsulaBsCaCeDnetEixDrE1EmaneEnameEsBtilEityCopiaCpalBucpCuDuEuFuGuHuAvacationCderCheClDentinIeErieGoDhallaDleyCmpireCnDceDessaDillaCrkeyCsantGhDonDsilioCughanBectrexFixCdayDderCeCljkoDoDvetCnceslasDdrediDetoDiceDkatGadHrGesDtureDusCrDilogDmontDnonDonicaGqueDseauDtigeGoDyCteransDteBianneyCbekeDhuDratorCcesquadDkiFeEyDtoireFrG1GiaHenGyCdeoCergeCgyanCjayFaCkingGsDramCllaFgeDmaCnayDceFntH1DitFhaDodFhColetEinCperF1CragoDgilFnGbirthGiaHeHoDusCsaEvisDhvjitDionEtFationForDpiDualDvanatCttorioCvekDianGeEenBjdayBladEimirCsiBmCsDsucksDucksBojinClcanoDleyGbDvoCodooCrtexCyagerBt100C52BvvDvEvFvGvHvAwadeCiDtingCldenEoDeedDidDkEerDleyeEyDterCnDdaEojoDgDkEerDtEmenowCrcraftDdDezDgamesDlockDmEweatherDnerDrenEiorHsDsDthogCsDhEingtonCterF1FlooDsonCvesCyDneF1BeCaselCbDetoysDheadDmasteIrDsterCdgeCenieEyDzerCiDdongDhengDnrichDpingClchEomeH1DlDsherCnDdelGlEiEyF1DgyikDtCreCsDleyDternCtBhale1FsDtEchamacallitEeveHrEnotEsupHdocCeelingFsDnDreFisthebeefFsthebeefDyCichDskyDtEeEingEneyCoDcaresDlesaleDopieFyDreDvilleCyBibbleCckedClburDdcatFhildDfriedDlEenEiamH1HsIburgFeEowEyDmaDsonCnD95DdEowGsEsurfDfredDgDnerEieGthepoohDonaDstonDterCredCsconsinDdomDhCthDnessfortheprosecutionCzardGsBojtekClfE1EgangEmanDverinIeFsCmanDbatG1DenCnDderGboyHreadDgDyunCobieFnEyDdElandErowEstocEwindEyDfwoofDiyiCpperkennyCrdDkDldDmwoodCuldBqsbBranglerCestleCightDteBunDtsinBwwDwEwFwGwHwBxyzByldchydCnneComingAx-filesCmenBanaduDthCvierGeBcountryBferCilesBgenerationBiCaoEboEgangEliEminCnghaoDuBmodemBrayBueDqingBwindowsBxfreessxxCpassxxCsnowxxCxD123DxExFxGxHxByzD123DzyAyBabbaF-dabba-dooFdabbadooCcoCelCmahaCnDgDjunDkeeGsCominCserBeeClloFwGstoneCngConEgCziBiannisCgalChuaCnDgEshaEyangCshunBodaDudeCgeshDibearCichiClandaCmamaCnDahDgEdongEhoFwanEsamCsemiteDhiakiFoCuD'reokDareokDcefDhanseDngDrEeokEselfBuanCehwernCgangChCjiEkoCkaDkeiDonCmiEkoCngCqianCvalBvesDtteConneByyDyEyFyGyHyAzacharyDkCpDataDhodCryBebraFsCna69DerFdiodeDithCphyrDpelinElinCusExBhaoqianEzhuaCengkunEyanCiDgangDshunDweiDxinCongguoFminBiggyDzagCmmermanCnfandelCtaCvCyouBmodemBoltanCmbieCndaComerCranDkEmidDoDroBuluBxcD123DvEbFnGmBz-topCtopCzDzEzFzGzHz";
	
	private void parseCommonWord()
	{
	   int i;
	   String c, word;
	   
	   i = 1;
	   c = CommonList.substring(i, i+1);
	   while (c == c.toLowerCase() && i < CommonList.length())
	   {
	      i++;
	      if ( i == CommonList.length() )
	    	  break;
	      c = CommonList.substring(i, i+1);
	   }
	   
	   word = CommonList.substring(0, i);
	   CommonList = CommonList.substring(i);
	   
	   if (word.substring(0, 1).equals("A"))
	   {
	      word = word.substring(1);
	   }
	   else
	   {
	      i = word.codePointAt(0) - "A".codePointAt(0);
	      word = CommonWords.get(CommonWords.size() - 1).substring(0, i) +
	         					word.substring(1);
	   }
	   
	   CommonWords.add(word);
	}

	private void parseCommonWords()
	{
	   while ( CommonList.length() > 0 )
	   {
	      parseCommonWord();
	   }
	}


	private void parseFrequencyToken()
	{
	   double c;
	   
	   c = FrequencyList.codePointAt(0) - " ".codePointAt(0);
	   c /= 95;
	   c += FrequencyList.codePointAt(1) - " ".codePointAt(0);
	   c /= 95;
	   c += FrequencyList.codePointAt(2) - " ".codePointAt(0);
	   c /= 95;
	   
	   FrequencyList = FrequencyList.substring(3);
	   
	   FrequencyTable.add(c);
	}


	private void parseFrequency()
	{// 3 because it is the minimun length need for parseFrequencyToken()
	   while ( FrequencyList.length() >= 3)  
	   {
	      parseFrequencyToken();
	   }
	}


	private int getIndex( String c)
	{
	   c = c.substring(0,1).toLowerCase();
	   if (c.compareTo("a") < 0|| c.compareTo("z") > 0)
	   {
	      return 0;
	   }
	   return c.codePointAt(0) - "a".codePointAt(0) + 1;
	}


	private int getCharsetSize(String  pass)
	{
	   int a = 0, u = 0, n = 0, ns = 0, r = 0, sp = 0, s = 0, chars = 0;
	   
	   for (int i = 0 ; i < pass.length(); i ++)
	   {
	      char c = pass.charAt(i);
	      if (a == 0 && "abcdefghijklmnopqrstuvwxyz".indexOf(c) >= 0)
	      {
	         chars += 26;
	         a = 1;
	      }
	      if (u == 0 && "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(c) >= 0)
	      {
	         chars += 26;
	         u = 1;
	      }
	      if (n == 0 && "0123456789".indexOf(c) >= 0)
	      {
	         chars += 10;
	         n = 1;
	      }
	      if (ns == 0 && "!@#$%^&*()".indexOf(c) >= 0)
	      {
	         chars += 10;
	         ns = 1;
	      }
	      if (r == 0 && "`~-_=+[{]}\\|;:'\",<.>/?".indexOf(c) >= 0)
	      {
	         chars += 20;
	         r = 1;
	      }
	      if (sp == 0 && c == ' ')
	      {
	         chars += 1;
	         sp = 1;
	      }
	      if (s == 0 && (c < ' ' || c > '~'))
	      {
	         chars += 32 + 128;
	         s = 1;
	      }
	   }
	   
	   return chars;
	}

	public PasswordCheck(){
		parseCommonWords();
		parseFrequency();
	}
	
	public int validatePassword(String pass) {
		if (pass.length() < 8)
		{
			return TOO_SHORT;
		}
  
		// First, see if it is a common password.
		for (String word : CommonWords)
		{
			if (word.equalsIgnoreCase(pass) )
			{
				return COMMON_WORD;
			}
		}
		String plower = pass.toLowerCase();
		// Calculate frequency chance
		if (pass.length() > 1)
		{
			int aidx = 0;
			double charSet,c , bits = 0;
			charSet = Math.log(getCharsetSize(pass));
			aidx = getIndex(plower.substring(0,1));
			for (int b = 1; b < plower.length(); b++)
			{
				int bidx = getIndex(plower.substring(b, b+1));
				c = 1.1 - FrequencyTable.get(aidx * 27 + bidx);
				bits += charSet * c * c;  // Squared = assmume they are good guessers
				aidx = bidx;
			}
      
			if (bits < 28)
			{
				return VERY_WEAK;
			}
			else if (bits < 36)
			{
				return WEAK;
			}
			else if (bits < 60)
			{
				return AVERAGE;
			}
			else if (bits < 128)
			{
				return STRONG;
			}
			else
			{
				return VERY_STRONG;
			}
		}
		return 0;
	}

}	
