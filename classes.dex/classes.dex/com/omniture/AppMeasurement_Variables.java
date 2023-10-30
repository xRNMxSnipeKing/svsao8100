package com.omniture;

import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.YouProfileModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import java.util.Hashtable;

public class AppMeasurement_Variables {
    public String acceptLanguage;
    public String account;
    protected String[] accountConfigList = new String[]{"account", "debugTracking", "trackOffline", "offlineLimit", "offlineThrottleDelay", "offlineFilename", "offlinePersistentStorageID", "configURL", "linkObject", "linkURL", "linkName", "linkType", "trackDownloadLinks", "trackExternalLinks", "trackClickMap", "linkLeaveQueryString", "linkTrackVars", "linkTrackEvents", "trackingServer", "trackingServerSecure", "ssl", "mobile", "dc", "lightTrackVars", "userAgent", "acceptLanguage"};
    protected String[] accountVarList = new String[]{"timestamp", "dynamicVariablePrefix", "visitorID", "vmk", "visitorMigrationKey", "visitorMigrationServer", "visitorMigrationServerSecure", "charSet", "visitorNamespace", "cookieDomainPeriods", "cookieLifetime", "pageName", "pageURL", "referrer", "contextData", "currencyCode", "lightProfileID", "lightStoreForSeconds", "lightIncrementBy", "retrieveLightProfiles", "deleteLightProfiles", "retrieveLightData", "purchaseID", "variableProvider", "channel", "server", "pageType", "transactionID", "campaign", "state", "zip", "events", "events2", "products", "tnt", "prop1", "eVar1", "hier1", "list1", "prop2", "eVar2", "hier2", "list2", "prop3", "eVar3", "hier3", "list3", "prop4", "eVar4", "hier4", "prop5", "eVar5", "hier5", "prop6", "eVar6", "prop7", "eVar7", "prop8", "eVar8", "prop9", "eVar9", "prop10", "eVar10", "prop11", "eVar11", "prop12", "eVar12", "prop13", "eVar13", "prop14", "eVar14", "prop15", "eVar15", "prop16", "eVar16", "prop17", "eVar17", "prop18", "eVar18", "prop19", "eVar19", "prop20", "eVar20", "prop21", "eVar21", "prop22", "eVar22", "prop23", "eVar23", "prop24", "eVar24", "prop25", "eVar25", "prop26", "eVar26", "prop27", "eVar27", "prop28", "eVar28", "prop29", "eVar29", "prop30", "eVar30", "prop31", "eVar31", "prop32", "eVar32", "prop33", "eVar33", "prop34", "eVar34", "prop35", "eVar35", "prop36", "eVar36", "prop37", "eVar37", "prop38", "eVar38", "prop39", "eVar39", "prop40", "eVar40", "prop41", "eVar41", "prop42", "eVar42", "prop43", "eVar43", "prop44", "eVar44", "prop45", "eVar45", "prop46", "eVar46", "prop47", "eVar47", "prop48", "eVar48", "prop49", "eVar49", "prop50", "eVar50", "prop51", "eVar51", "prop52", "eVar52", "prop53", "eVar53", "prop54", "eVar54", "prop55", "eVar55", "prop56", "eVar56", "prop57", "eVar57", "prop58", "eVar58", "prop59", "eVar59", "prop60", "eVar60", "prop61", "eVar61", "prop62", "eVar62", "prop63", "eVar63", "prop64", "eVar64", "prop65", "eVar65", "prop66", "eVar66", "prop67", "eVar67", "prop68", "eVar68", "prop69", "eVar69", "prop70", "eVar70", "prop71", "eVar71", "prop72", "eVar72", "prop73", "eVar73", "prop74", "eVar74", "prop75", "eVar75", "pe", "pev1", "pev2", "pev3", "resolution"};
    protected Hashtable accountVars;
    public String campaign;
    public String channel;
    public String charSet;
    public String configURL;
    public Hashtable contextData;
    public int cookieDomainPeriods;
    public String cookieLifetime;
    public String currencyCode;
    public String dc;
    public boolean debugTracking;
    public String deleteLightProfiles;
    public String dynamicVariablePrefix;
    public String eVar1;
    public String eVar10;
    public String eVar11;
    public String eVar12;
    public String eVar13;
    public String eVar14;
    public String eVar15;
    public String eVar16;
    public String eVar17;
    public String eVar18;
    public String eVar19;
    public String eVar2;
    public String eVar20;
    public String eVar21;
    public String eVar22;
    public String eVar23;
    public String eVar24;
    public String eVar25;
    public String eVar26;
    public String eVar27;
    public String eVar28;
    public String eVar29;
    public String eVar3;
    public String eVar30;
    public String eVar31;
    public String eVar32;
    public String eVar33;
    public String eVar34;
    public String eVar35;
    public String eVar36;
    public String eVar37;
    public String eVar38;
    public String eVar39;
    public String eVar4;
    public String eVar40;
    public String eVar41;
    public String eVar42;
    public String eVar43;
    public String eVar44;
    public String eVar45;
    public String eVar46;
    public String eVar47;
    public String eVar48;
    public String eVar49;
    public String eVar5;
    public String eVar50;
    public String eVar51;
    public String eVar52;
    public String eVar53;
    public String eVar54;
    public String eVar55;
    public String eVar56;
    public String eVar57;
    public String eVar58;
    public String eVar59;
    public String eVar6;
    public String eVar60;
    public String eVar61;
    public String eVar62;
    public String eVar63;
    public String eVar64;
    public String eVar65;
    public String eVar66;
    public String eVar67;
    public String eVar68;
    public String eVar69;
    public String eVar7;
    public String eVar70;
    public String eVar71;
    public String eVar72;
    public String eVar73;
    public String eVar74;
    public String eVar75;
    public String eVar8;
    public String eVar9;
    public String events;
    public String events2;
    public String hier1;
    public String hier2;
    public String hier3;
    public String hier4;
    public String hier5;
    public int lightIncrementBy;
    public String lightProfileID;
    protected String[] lightRequiredVarList = new String[]{"timestamp", "charSet", "visitorNamespace", "cookieDomainPeriods", "cookieLifetime", "contextData", "lightProfileID", "lightStoreForSeconds", "lightIncrementBy"};
    public int lightStoreForSeconds;
    public String lightTrackVars;
    protected String[] lightVarList = new String[]{"timestamp", "charSet", "visitorNamespace", "cookieDomainPeriods", "cookieLifetime", "contextData", "lightProfileID", "lightStoreForSeconds", "lightIncrementBy", "prop1", "eVar1", "prop2", "eVar2", "prop3", "eVar3", "prop4", "eVar4", "prop5", "eVar5", "prop6", "eVar6", "prop7", "eVar7", "prop8", "eVar8", "prop9", "eVar9", "prop10", "eVar10", "prop11", "eVar11", "prop12", "eVar12", "prop13", "eVar13", "prop14", "eVar14", "prop15", "eVar15", "prop16", "eVar16", "prop17", "eVar17", "prop18", "eVar18", "prop19", "eVar19", "prop20", "eVar20", "prop21", "eVar21", "prop22", "eVar22", "prop23", "eVar23", "prop24", "eVar24", "prop25", "eVar25", "prop26", "eVar26", "prop27", "eVar27", "prop28", "eVar28", "prop29", "eVar29", "prop30", "eVar30", "prop31", "eVar31", "prop32", "eVar32", "prop33", "eVar33", "prop34", "eVar34", "prop35", "eVar35", "prop36", "eVar36", "prop37", "eVar37", "prop38", "eVar38", "prop39", "eVar39", "prop40", "eVar40", "prop41", "eVar41", "prop42", "eVar42", "prop43", "eVar43", "prop44", "eVar44", "prop45", "eVar45", "prop46", "eVar46", "prop47", "eVar47", "prop48", "eVar48", "prop49", "eVar49", "prop50", "eVar50", "prop51", "eVar51", "prop52", "eVar52", "prop53", "eVar53", "prop54", "eVar54", "prop55", "eVar55", "prop56", "eVar56", "prop57", "eVar57", "prop58", "eVar58", "prop59", "eVar59", "prop60", "eVar60", "prop61", "eVar61", "prop62", "eVar62", "prop63", "eVar63", "prop64", "eVar64", "prop65", "eVar65", "prop66", "eVar66", "prop67", "eVar67", "prop68", "eVar68", "prop69", "eVar69", "prop70", "eVar70", "prop71", "eVar71", "prop72", "eVar72", "prop73", "eVar73", "prop74", "eVar74", "prop75", "eVar75"};
    public boolean linkLeaveQueryString;
    public String linkName;
    public String linkObject;
    public String linkTrackEvents;
    public String linkTrackVars;
    public String linkType;
    public String linkURL;
    public String list1;
    public String list2;
    public String list3;
    public boolean mobile;
    public String offlineFilename;
    public int offlineLimit;
    public long offlinePersistentStorageID;
    public int offlineThrottleDelay;
    public String pageName;
    public String pageType;
    public String pageURL;
    public String pe;
    public String pev1;
    public String pev2;
    public String pev3;
    public String products;
    public String prop1;
    public String prop10;
    public String prop11;
    public String prop12;
    public String prop13;
    public String prop14;
    public String prop15;
    public String prop16;
    public String prop17;
    public String prop18;
    public String prop19;
    public String prop2;
    public String prop20;
    public String prop21;
    public String prop22;
    public String prop23;
    public String prop24;
    public String prop25;
    public String prop26;
    public String prop27;
    public String prop28;
    public String prop29;
    public String prop3;
    public String prop30;
    public String prop31;
    public String prop32;
    public String prop33;
    public String prop34;
    public String prop35;
    public String prop36;
    public String prop37;
    public String prop38;
    public String prop39;
    public String prop4;
    public String prop40;
    public String prop41;
    public String prop42;
    public String prop43;
    public String prop44;
    public String prop45;
    public String prop46;
    public String prop47;
    public String prop48;
    public String prop49;
    public String prop5;
    public String prop50;
    public String prop51;
    public String prop52;
    public String prop53;
    public String prop54;
    public String prop55;
    public String prop56;
    public String prop57;
    public String prop58;
    public String prop59;
    public String prop6;
    public String prop60;
    public String prop61;
    public String prop62;
    public String prop63;
    public String prop64;
    public String prop65;
    public String prop66;
    public String prop67;
    public String prop68;
    public String prop69;
    public String prop7;
    public String prop70;
    public String prop71;
    public String prop72;
    public String prop73;
    public String prop74;
    public String prop75;
    public String prop8;
    public String prop9;
    public String purchaseID;
    public String referrer;
    protected String[] requiredVarList = new String[]{"timestamp", "dynamicVariablePrefix", "visitorID", "vmk", "visitorMigrationKey", "visitorMigrationServer", "visitorMigrationServerSecure", "charSet", "visitorNamespace", "cookieDomainPeriods", "cookieLifetime", "pageName", "pageURL", "referrer", "contextData", "currencyCode", "lightProfileID", "lightStoreForSeconds", "lightIncrementBy", "retrieveLightProfiles", "deleteLightProfiles", "retrieveLightData", "pe", "pev1", "pev2", "pev3", "resolution"};
    public String resolution;
    public Hashtable retrieveLightData;
    public String retrieveLightProfiles;
    public String server;
    public boolean ssl;
    public String state;
    public int timestamp;
    public String tnt;
    public boolean trackClickMap;
    public boolean trackDownloadLinks;
    public boolean trackExternalLinks;
    public boolean trackOffline;
    public String trackingServer;
    public String trackingServerSecure;
    public String transactionID;
    public String userAgent;
    public String variableProvider;
    public String visitorID;
    public String visitorMigrationKey;
    public String visitorMigrationServer;
    public String visitorMigrationServerSecure;
    public String visitorNamespace;
    public String vmk;
    public String zip;

    public AppMeasurement_Variables() {
        setupAccountVars();
    }

    protected void setupAccountVars() {
        this.accountVars = new Hashtable();
        this.accountVars.put("account", new Integer(0));
        this.accountVars.put("debugTracking", new Integer(1));
        this.accountVars.put("trackOffline", new Integer(2));
        this.accountVars.put("offlineLimit", new Integer(3));
        this.accountVars.put("offlineThrottleDelay", new Integer(4));
        this.accountVars.put("offlineFilename", new Integer(5));
        this.accountVars.put("offlinePersistentStorageID", new Integer(6));
        this.accountVars.put("configURL", new Integer(7));
        this.accountVars.put("linkObject", new Integer(8));
        this.accountVars.put("linkURL", new Integer(9));
        this.accountVars.put("linkName", new Integer(10));
        this.accountVars.put("linkType", new Integer(11));
        this.accountVars.put("trackDownloadLinks", new Integer(12));
        this.accountVars.put("trackExternalLinks", new Integer(13));
        this.accountVars.put("trackClickMap", new Integer(14));
        this.accountVars.put("linkLeaveQueryString", new Integer(15));
        this.accountVars.put("linkTrackVars", new Integer(16));
        this.accountVars.put("linkTrackEvents", new Integer(17));
        this.accountVars.put("trackingServer", new Integer(18));
        this.accountVars.put("trackingServerSecure", new Integer(19));
        this.accountVars.put("ssl", new Integer(20));
        this.accountVars.put("mobile", new Integer(21));
        this.accountVars.put("dc", new Integer(22));
        this.accountVars.put("lightTrackVars", new Integer(23));
        this.accountVars.put("userAgent", new Integer(24));
        this.accountVars.put("acceptLanguage", new Integer(25));
        this.accountVars.put("timestamp", new Integer(26));
        this.accountVars.put("dynamicVariablePrefix", new Integer(27));
        this.accountVars.put("visitorID", new Integer(28));
        this.accountVars.put("vmk", new Integer(29));
        this.accountVars.put("visitorMigrationKey", new Integer(30));
        this.accountVars.put("visitorMigrationServer", new Integer(31));
        this.accountVars.put("visitorMigrationServerSecure", new Integer(32));
        this.accountVars.put("charSet", new Integer(33));
        this.accountVars.put("visitorNamespace", new Integer(34));
        this.accountVars.put("cookieDomainPeriods", new Integer(35));
        this.accountVars.put("cookieLifetime", new Integer(36));
        this.accountVars.put("pageName", new Integer(37));
        this.accountVars.put("pageURL", new Integer(38));
        this.accountVars.put("referrer", new Integer(39));
        this.accountVars.put("contextData", new Integer(40));
        this.accountVars.put("currencyCode", new Integer(41));
        this.accountVars.put("lightProfileID", new Integer(42));
        this.accountVars.put("lightStoreForSeconds", new Integer(43));
        this.accountVars.put("lightIncrementBy", new Integer(44));
        this.accountVars.put("retrieveLightProfiles", new Integer(45));
        this.accountVars.put("deleteLightProfiles", new Integer(46));
        this.accountVars.put("retrieveLightData", new Integer(47));
        this.accountVars.put("purchaseID", new Integer(48));
        this.accountVars.put("variableProvider", new Integer(49));
        this.accountVars.put("channel", new Integer(50));
        this.accountVars.put("server", new Integer(51));
        this.accountVars.put("pageType", new Integer(52));
        this.accountVars.put("transactionID", new Integer(53));
        this.accountVars.put("campaign", new Integer(54));
        this.accountVars.put("state", new Integer(55));
        this.accountVars.put("zip", new Integer(56));
        this.accountVars.put("events", new Integer(57));
        this.accountVars.put("events2", new Integer(58));
        this.accountVars.put("products", new Integer(59));
        this.accountVars.put("tnt", new Integer(60));
        this.accountVars.put("prop1", new Integer(61));
        this.accountVars.put("eVar1", new Integer(62));
        this.accountVars.put("hier1", new Integer(63));
        this.accountVars.put("list1", new Integer(64));
        this.accountVars.put("prop2", new Integer(65));
        this.accountVars.put("eVar2", new Integer(66));
        this.accountVars.put("hier2", new Integer(67));
        this.accountVars.put("list2", new Integer(68));
        this.accountVars.put("prop3", new Integer(69));
        this.accountVars.put("eVar3", new Integer(70));
        this.accountVars.put("hier3", new Integer(71));
        this.accountVars.put("list3", new Integer(72));
        this.accountVars.put("prop4", new Integer(73));
        this.accountVars.put("eVar4", new Integer(74));
        this.accountVars.put("hier4", new Integer(75));
        this.accountVars.put("prop5", new Integer(76));
        this.accountVars.put("eVar5", new Integer(77));
        this.accountVars.put("hier5", new Integer(78));
        this.accountVars.put("prop6", new Integer(79));
        this.accountVars.put("eVar6", new Integer(80));
        this.accountVars.put("prop7", new Integer(81));
        this.accountVars.put("eVar7", new Integer(82));
        this.accountVars.put("prop8", new Integer(83));
        this.accountVars.put("eVar8", new Integer(84));
        this.accountVars.put("prop9", new Integer(85));
        this.accountVars.put("eVar9", new Integer(86));
        this.accountVars.put("prop10", new Integer(87));
        this.accountVars.put("eVar10", new Integer(88));
        this.accountVars.put("prop11", new Integer(89));
        this.accountVars.put("eVar11", new Integer(90));
        this.accountVars.put("prop12", new Integer(91));
        this.accountVars.put("eVar12", new Integer(92));
        this.accountVars.put("prop13", new Integer(93));
        this.accountVars.put("eVar13", new Integer(94));
        this.accountVars.put("prop14", new Integer(95));
        this.accountVars.put("eVar14", new Integer(96));
        this.accountVars.put("prop15", new Integer(97));
        this.accountVars.put("eVar15", new Integer(98));
        this.accountVars.put("prop16", new Integer(99));
        this.accountVars.put("eVar16", new Integer(100));
        this.accountVars.put("prop17", new Integer(101));
        this.accountVars.put("eVar17", new Integer(102));
        this.accountVars.put("prop18", new Integer(103));
        this.accountVars.put("eVar18", new Integer(104));
        this.accountVars.put("prop19", new Integer(105));
        this.accountVars.put("eVar19", new Integer(106));
        this.accountVars.put("prop20", new Integer(107));
        this.accountVars.put("eVar20", new Integer(108));
        this.accountVars.put("prop21", new Integer(109));
        this.accountVars.put("eVar21", new Integer(110));
        this.accountVars.put("prop22", new Integer(111));
        this.accountVars.put("eVar22", new Integer(112));
        this.accountVars.put("prop23", new Integer(113));
        this.accountVars.put("eVar23", new Integer(114));
        this.accountVars.put("prop24", new Integer(115));
        this.accountVars.put("eVar24", new Integer(116));
        this.accountVars.put("prop25", new Integer(117));
        this.accountVars.put("eVar25", new Integer(118));
        this.accountVars.put("prop26", new Integer(119));
        this.accountVars.put("eVar26", new Integer(120));
        this.accountVars.put("prop27", new Integer(121));
        this.accountVars.put("eVar27", new Integer(122));
        this.accountVars.put("prop28", new Integer(123));
        this.accountVars.put("eVar28", new Integer(124));
        this.accountVars.put("prop29", new Integer(125));
        this.accountVars.put("eVar29", new Integer(126));
        this.accountVars.put("prop30", new Integer(127));
        this.accountVars.put("eVar30", new Integer(128));
        this.accountVars.put("prop31", new Integer(129));
        this.accountVars.put("eVar31", new Integer(130));
        this.accountVars.put("prop32", new Integer(131));
        this.accountVars.put("eVar32", new Integer(132));
        this.accountVars.put("prop33", new Integer(133));
        this.accountVars.put("eVar33", new Integer(134));
        this.accountVars.put("prop34", new Integer(135));
        this.accountVars.put("eVar34", new Integer(136));
        this.accountVars.put("prop35", new Integer(137));
        this.accountVars.put("eVar35", new Integer(138));
        this.accountVars.put("prop36", new Integer(139));
        this.accountVars.put("eVar36", new Integer(140));
        this.accountVars.put("prop37", new Integer(141));
        this.accountVars.put("eVar37", new Integer(142));
        this.accountVars.put("prop38", new Integer(143));
        this.accountVars.put("eVar38", new Integer(144));
        this.accountVars.put("prop39", new Integer(145));
        this.accountVars.put("eVar39", new Integer(146));
        this.accountVars.put("prop40", new Integer(147));
        this.accountVars.put("eVar40", new Integer(148));
        this.accountVars.put("prop41", new Integer(149));
        this.accountVars.put("eVar41", new Integer(150));
        this.accountVars.put("prop42", new Integer(151));
        this.accountVars.put("eVar42", new Integer(152));
        this.accountVars.put("prop43", new Integer(153));
        this.accountVars.put("eVar43", new Integer(154));
        this.accountVars.put("prop44", new Integer(155));
        this.accountVars.put("eVar44", new Integer(156));
        this.accountVars.put("prop45", new Integer(157));
        this.accountVars.put("eVar45", new Integer(158));
        this.accountVars.put("prop46", new Integer(159));
        this.accountVars.put("eVar46", new Integer(160));
        this.accountVars.put("prop47", new Integer(161));
        this.accountVars.put("eVar47", new Integer(162));
        this.accountVars.put("prop48", new Integer(163));
        this.accountVars.put("eVar48", new Integer(164));
        this.accountVars.put("prop49", new Integer(165));
        this.accountVars.put("eVar49", new Integer(166));
        this.accountVars.put("prop50", new Integer(167));
        this.accountVars.put("eVar50", new Integer(168));
        this.accountVars.put("prop51", new Integer(169));
        this.accountVars.put("eVar51", new Integer(170));
        this.accountVars.put("prop52", new Integer(171));
        this.accountVars.put("eVar52", new Integer(172));
        this.accountVars.put("prop53", new Integer(173));
        this.accountVars.put("eVar53", new Integer(174));
        this.accountVars.put("prop54", new Integer(175));
        this.accountVars.put("eVar54", new Integer(176));
        this.accountVars.put("prop55", new Integer(177));
        this.accountVars.put("eVar55", new Integer(178));
        this.accountVars.put("prop56", new Integer(179));
        this.accountVars.put("eVar56", new Integer(180));
        this.accountVars.put("prop57", new Integer(181));
        this.accountVars.put("eVar57", new Integer(182));
        this.accountVars.put("prop58", new Integer(183));
        this.accountVars.put("eVar58", new Integer(184));
        this.accountVars.put("prop59", new Integer(185));
        this.accountVars.put("eVar59", new Integer(186));
        this.accountVars.put("prop60", new Integer(187));
        this.accountVars.put("eVar60", new Integer(188));
        this.accountVars.put("prop61", new Integer(189));
        this.accountVars.put("eVar61", new Integer(190));
        this.accountVars.put("prop62", new Integer(191));
        this.accountVars.put("eVar62", new Integer(192));
        this.accountVars.put("prop63", new Integer(193));
        this.accountVars.put("eVar63", new Integer(194));
        this.accountVars.put("prop64", new Integer(195));
        this.accountVars.put("eVar64", new Integer(196));
        this.accountVars.put("prop65", new Integer(197));
        this.accountVars.put("eVar65", new Integer(198));
        this.accountVars.put("prop66", new Integer(199));
        this.accountVars.put("eVar66", new Integer(200));
        this.accountVars.put("prop67", new Integer(201));
        this.accountVars.put("eVar67", new Integer(202));
        this.accountVars.put("prop68", new Integer(203));
        this.accountVars.put("eVar68", new Integer(204));
        this.accountVars.put("prop69", new Integer(205));
        this.accountVars.put("eVar69", new Integer(206));
        this.accountVars.put("prop70", new Integer(207));
        this.accountVars.put("eVar70", new Integer(208));
        this.accountVars.put("prop71", new Integer(209));
        this.accountVars.put("eVar71", new Integer(210));
        this.accountVars.put("prop72", new Integer(211));
        this.accountVars.put("eVar72", new Integer(212));
        this.accountVars.put("prop73", new Integer(213));
        this.accountVars.put("eVar73", new Integer(214));
        this.accountVars.put("prop74", new Integer(215));
        this.accountVars.put("eVar74", new Integer(216));
        this.accountVars.put("prop75", new Integer(217));
        this.accountVars.put("eVar75", new Integer(218));
        this.accountVars.put("pe", new Integer(219));
        this.accountVars.put("pev1", new Integer(220));
        this.accountVars.put("pev2", new Integer(221));
        this.accountVars.put("pev3", new Integer(222));
        this.accountVars.put("resolution", new Integer(223));
    }

    protected String getAccountVar(String key) {
        try {
            switch (((Integer) this.accountVars.get(key)).intValue()) {
                case 0:
                    return this.account;
                case 1:
                    if (this.debugTracking) {
                        return "true";
                    }
                    return "false";
                case 2:
                    if (this.trackOffline) {
                        return "true";
                    }
                    return "false";
                case 3:
                    if (this.offlineLimit != 0) {
                        return Integer.toString(this.offlineLimit);
                    }
                    return null;
                case 4:
                    if (this.offlineThrottleDelay != 0) {
                        return Integer.toString(this.offlineThrottleDelay);
                    }
                    return null;
                case 5:
                    return this.offlineFilename;
                case 6:
                    if (this.offlinePersistentStorageID != 0) {
                        return Long.toString(this.offlinePersistentStorageID);
                    }
                    return null;
                case 7:
                    return this.configURL;
                case 8:
                    return this.linkObject;
                case 9:
                    return this.linkURL;
                case 10:
                    return this.linkName;
                case 11:
                    return this.linkType;
                case CompanionSession.LRCERROR_TOO_MANY_CLIENTS /*12*/:
                    if (this.trackDownloadLinks) {
                        return "true";
                    }
                    return "false";
                case CompanionSession.LRCERROR_EXPIRED_COMMAND /*13*/:
                    if (this.trackExternalLinks) {
                        return "true";
                    }
                    return "false";
                case CompanionSession.LRCERROR_NO_EXCLUSIVE /*14*/:
                    if (this.trackClickMap) {
                        return "true";
                    }
                    return "false";
                case 15:
                    if (this.linkLeaveQueryString) {
                        return "true";
                    }
                    return "false";
                case 16:
                    return this.linkTrackVars;
                case CompanionSession.LRCERROR_TITLECHANNEL_EXISTS /*17*/:
                    return this.linkTrackEvents;
                case EDSV2MediaType.MEDIATYPE_XBOX360GAMECONTENT /*18*/:
                    return this.trackingServer;
                case EDSV2MediaType.MEDIATYPE_XBOX360GAMEDEMO /*19*/:
                    return this.trackingServerSecure;
                case 20:
                    if (this.ssl) {
                        return "true";
                    }
                    return "false";
                case EDSV2MediaType.MEDIATYPE_XBOXORIGINALGAME /*21*/:
                    if (this.mobile) {
                        return "true";
                    }
                    return "false";
                case EDSV2MediaType.MEDIATYPE_XBOXGAMERTILE /*22*/:
                    return this.dc;
                case EDSV2MediaType.MEDIATYPE_XBOXARCADEGAME /*23*/:
                    return this.lightTrackVars;
                case EDSV2MediaType.MEDIATYPE_XBOXGAMECONSUMABLE /*24*/:
                    return this.userAgent;
                case 25:
                    return this.acceptLanguage;
                case 26:
                    if (this.timestamp != 0) {
                        return Integer.toString(this.timestamp);
                    }
                    return null;
                case 27:
                    return this.dynamicVariablePrefix;
                case 28:
                    return this.visitorID;
                case 29:
                    return this.vmk;
                case EDSV2MediaType.MEDIATYPE_XBOXGAMEVIDEO /*30*/:
                    return this.visitorMigrationKey;
                case 31:
                    return this.visitorMigrationServer;
                case 32:
                    return this.visitorMigrationServerSecure;
                case 33:
                    return this.charSet;
                case EDSV2MediaType.MEDIATYPE_XBOXGAMETRAILER /*34*/:
                    return this.visitorNamespace;
                case 35:
                    if (this.cookieDomainPeriods != 0) {
                        return Integer.toString(this.cookieDomainPeriods);
                    }
                    return null;
                case EDSV2MediaType.MEDIATYPE_XBOXBUNDLE /*36*/:
                    return this.cookieLifetime;
                case EDSV2MediaType.MEDIATYPE_XBOXXNACOMMUNITYGAME /*37*/:
                    return this.pageName;
                case 38:
                    return this.pageURL;
                case 39:
                    return this.referrer;
                case 40:
                    if (this.contextData != null) {
                        return "NOT-NULL";
                    }
                    return null;
                case YouProfileModel.YOU_PROFILE_SECTIONS /*41*/:
                    return this.currencyCode;
                case 42:
                    return this.lightProfileID;
                case 43:
                    if (this.lightStoreForSeconds != 0) {
                        return Integer.toString(this.lightStoreForSeconds);
                    }
                    return null;
                case 44:
                    if (this.lightIncrementBy != 0) {
                        return Integer.toString(this.lightIncrementBy);
                    }
                    return null;
                case 45:
                    return this.retrieveLightProfiles;
                case EDSV2MediaType.MEDIATYPE_XBOXMARKETPLACE /*46*/:
                    return this.deleteLightProfiles;
                case EDSV2MediaType.MEDIATYPE_AVATARITEM /*47*/:
                    if (this.retrieveLightData != null) {
                        return "NOT-NULL";
                    }
                    return null;
                case 48:
                    return this.purchaseID;
                case 49:
                    return this.variableProvider;
                case 50:
                    return this.channel;
                case 51:
                    return this.server;
                case 52:
                    return this.pageType;
                case 53:
                    return this.transactionID;
                case 54:
                    return this.campaign;
                case 55:
                    return this.state;
                case 56:
                    return this.zip;
                case EDSV2MediaType.MEDIATYPE_WEBGAME /*57*/:
                    return this.events;
                case EDSV2MediaType.MEDIATYPE_MOBILEGAME /*58*/:
                    return this.events2;
                case EDSV2MediaType.MEDIATYPE_XBOXMOBILEPDLC /*59*/:
                    return this.products;
                case EDSV2MediaType.MEDIATYPE_XBOXMOBILECONSUMABLE /*60*/:
                    return this.tnt;
                case EDSV2MediaType.MEDIATYPE_XBOXAPP /*61*/:
                    return this.prop1;
                case EDSV2MediaType.MEDIATYPE_METROGAME /*62*/:
                    return this.eVar1;
                case EDSV2MediaType.MEDIATYPE_METROGAMECONTENT /*63*/:
                    return this.hier1;
                case 64:
                    return this.list1;
                case EDSV2MediaType.MEDIATYPE_GAMELAYER /*65*/:
                    return this.prop2;
                case EDSV2MediaType.MEDIATYPE_GAMEACTIVITY /*66*/:
                    return this.eVar2;
                case EDSV2MediaType.MEDIATYPE_APPACTIVITY /*67*/:
                    return this.hier2;
                case 68:
                    return this.list2;
                case 69:
                    return this.prop3;
                case 70:
                    return this.eVar3;
                case 71:
                    return this.hier3;
                case 72:
                    return this.list3;
                case 73:
                    return this.prop4;
                case 74:
                    return this.eVar4;
                case 75:
                    return this.hier4;
                case 76:
                    return this.prop5;
                case 77:
                    return this.eVar5;
                case 78:
                    return this.hier5;
                case 79:
                    return this.prop6;
                case 80:
                    return this.eVar6;
                case 81:
                    return this.prop7;
                case 82:
                    return this.eVar7;
                case 83:
                    return this.prop8;
                case 84:
                    return this.eVar8;
                case 85:
                    return this.prop9;
                case 86:
                    return this.eVar9;
                case 87:
                    return this.prop10;
                case 88:
                    return this.eVar10;
                case 89:
                    return this.prop11;
                case 90:
                    return this.eVar11;
                case 91:
                    return this.prop12;
                case 92:
                    return this.eVar12;
                case 93:
                    return this.prop13;
                case 94:
                    return this.eVar13;
                case 95:
                    return this.prop14;
                case 96:
                    return this.eVar14;
                case MeProfileModel.ME_PROFILE_SECTIONS /*97*/:
                    return this.prop15;
                case 98:
                    return this.eVar15;
                case 99:
                    return this.prop16;
                case 100:
                    return this.eVar16;
                case 101:
                    return this.prop17;
                case 102:
                    return this.eVar17;
                case 103:
                    return this.prop18;
                case 104:
                    return this.eVar18;
                case 105:
                    return this.prop19;
                case 106:
                    return this.eVar19;
                case 107:
                    return this.prop20;
                case 108:
                    return this.eVar20;
                case 109:
                    return this.prop21;
                case 110:
                    return this.eVar21;
                case 111:
                    return this.prop22;
                case 112:
                    return this.eVar22;
                case 113:
                    return this.prop23;
                case 114:
                    return this.eVar23;
                case 115:
                    return this.prop24;
                case 116:
                    return this.eVar24;
                case 117:
                    return this.prop25;
                case 118:
                    return this.eVar25;
                case 119:
                    return this.prop26;
                case 120:
                    return this.eVar26;
                case 121:
                    return this.prop27;
                case 122:
                    return this.eVar27;
                case 123:
                    return this.prop28;
                case 124:
                    return this.eVar28;
                case 125:
                    return this.prop29;
                case 126:
                    return this.eVar29;
                case 127:
                    return this.prop30;
                case 128:
                    return this.eVar30;
                case 129:
                    return this.prop31;
                case 130:
                    return this.eVar31;
                case 131:
                    return this.prop32;
                case 132:
                    return this.eVar32;
                case 133:
                    return this.prop33;
                case 134:
                    return this.eVar33;
                case 135:
                    return this.prop34;
                case 136:
                    return this.eVar34;
                case 137:
                    return this.prop35;
                case 138:
                    return this.eVar35;
                case 139:
                    return this.prop36;
                case 140:
                    return this.eVar36;
                case 141:
                    return this.prop37;
                case 142:
                    return this.eVar37;
                case 143:
                    return this.prop38;
                case 144:
                    return this.eVar38;
                case 145:
                    return this.prop39;
                case 146:
                    return this.eVar39;
                case 147:
                    return this.prop40;
                case 148:
                    return this.eVar40;
                case 149:
                    return this.prop41;
                case 150:
                    return this.eVar41;
                case 151:
                    return this.prop42;
                case 152:
                    return this.eVar42;
                case 153:
                    return this.prop43;
                case 154:
                    return this.eVar43;
                case 155:
                    return this.prop44;
                case 156:
                    return this.eVar44;
                case 157:
                    return this.prop45;
                case 158:
                    return this.eVar45;
                case 159:
                    return this.prop46;
                case 160:
                    return this.eVar46;
                case 161:
                    return this.prop47;
                case 162:
                    return this.eVar47;
                case 163:
                    return this.prop48;
                case 164:
                    return this.eVar48;
                case 165:
                    return this.prop49;
                case 166:
                    return this.eVar49;
                case 167:
                    return this.prop50;
                case 168:
                    return this.eVar50;
                case 169:
                    return this.prop51;
                case 170:
                    return this.eVar51;
                case 171:
                    return this.prop52;
                case 172:
                    return this.eVar52;
                case 173:
                    return this.prop53;
                case 174:
                    return this.eVar53;
                case 175:
                    return this.prop54;
                case 176:
                    return this.eVar54;
                case 177:
                    return this.prop55;
                case 178:
                    return this.eVar55;
                case 179:
                    return this.prop56;
                case 180:
                    return this.eVar56;
                case 181:
                    return this.prop57;
                case 182:
                    return this.eVar57;
                case 183:
                    return this.prop58;
                case 184:
                    return this.eVar58;
                case 185:
                    return this.prop59;
                case 186:
                    return this.eVar59;
                case 187:
                    return this.prop60;
                case 188:
                    return this.eVar60;
                case 189:
                    return this.prop61;
                case 190:
                    return this.eVar61;
                case 191:
                    return this.prop62;
                case 192:
                    return this.eVar62;
                case 193:
                    return this.prop63;
                case 194:
                    return this.eVar63;
                case 195:
                    return this.prop64;
                case 196:
                    return this.eVar64;
                case 197:
                    return this.prop65;
                case 198:
                    return this.eVar65;
                case 199:
                    return this.prop66;
                case 200:
                    return this.eVar66;
                case 201:
                    return this.prop67;
                case 202:
                    return this.eVar67;
                case 203:
                    return this.prop68;
                case 204:
                    return this.eVar68;
                case 205:
                    return this.prop69;
                case 206:
                    return this.eVar69;
                case 207:
                    return this.prop70;
                case 208:
                    return this.eVar70;
                case 209:
                    return this.prop71;
                case 210:
                    return this.eVar71;
                case 211:
                    return this.prop72;
                case 212:
                    return this.eVar72;
                case 213:
                    return this.prop73;
                case 214:
                    return this.eVar73;
                case 215:
                    return this.prop74;
                case 216:
                    return this.eVar74;
                case 217:
                    return this.prop75;
                case 218:
                    return this.eVar75;
                case 219:
                    return this.pe;
                case 220:
                    return this.pev1;
                case 221:
                    return this.pev2;
                case 222:
                    return this.pev3;
                case 223:
                    return this.resolution;
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    protected void setAccountVar(String key, String value) {
        try {
            switch (((Integer) this.accountVars.get(key)).intValue()) {
                case 0:
                    this.account = value;
                    return;
                case 1:
                    if (value.equals("true")) {
                        this.debugTracking = true;
                        return;
                    } else {
                        this.debugTracking = false;
                        return;
                    }
                case 2:
                    if (value.equals("true")) {
                        this.trackOffline = true;
                        return;
                    } else {
                        this.trackOffline = false;
                        return;
                    }
                case 3:
                    this.offlineLimit = Integer.parseInt(value);
                    return;
                case 4:
                    this.offlineThrottleDelay = Integer.parseInt(value);
                    return;
                case 5:
                    this.offlineFilename = value;
                    return;
                case 6:
                    this.offlinePersistentStorageID = Long.parseLong(value);
                    return;
                case 7:
                    this.configURL = value;
                    return;
                case 8:
                    this.linkObject = value;
                    return;
                case 9:
                    this.linkURL = value;
                    return;
                case 10:
                    this.linkName = value;
                    return;
                case 11:
                    this.linkType = value;
                    return;
                case CompanionSession.LRCERROR_TOO_MANY_CLIENTS /*12*/:
                    if (value.equals("true")) {
                        this.trackDownloadLinks = true;
                        return;
                    } else {
                        this.trackDownloadLinks = false;
                        return;
                    }
                case CompanionSession.LRCERROR_EXPIRED_COMMAND /*13*/:
                    if (value.equals("true")) {
                        this.trackExternalLinks = true;
                        return;
                    } else {
                        this.trackExternalLinks = false;
                        return;
                    }
                case CompanionSession.LRCERROR_NO_EXCLUSIVE /*14*/:
                    if (value.equals("true")) {
                        this.trackClickMap = true;
                        return;
                    } else {
                        this.trackClickMap = false;
                        return;
                    }
                case 15:
                    if (value.equals("true")) {
                        this.linkLeaveQueryString = true;
                        return;
                    } else {
                        this.linkLeaveQueryString = false;
                        return;
                    }
                case 16:
                    this.linkTrackVars = value;
                    return;
                case CompanionSession.LRCERROR_TITLECHANNEL_EXISTS /*17*/:
                    this.linkTrackEvents = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_XBOX360GAMECONTENT /*18*/:
                    this.trackingServer = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_XBOX360GAMEDEMO /*19*/:
                    this.trackingServerSecure = value;
                    return;
                case 20:
                    if (value.equals("true")) {
                        this.ssl = true;
                        return;
                    } else {
                        this.ssl = false;
                        return;
                    }
                case EDSV2MediaType.MEDIATYPE_XBOXORIGINALGAME /*21*/:
                    if (value.equals("true")) {
                        this.mobile = true;
                        return;
                    } else {
                        this.mobile = false;
                        return;
                    }
                case EDSV2MediaType.MEDIATYPE_XBOXGAMERTILE /*22*/:
                    this.dc = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_XBOXARCADEGAME /*23*/:
                    this.lightTrackVars = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_XBOXGAMECONSUMABLE /*24*/:
                    this.userAgent = value;
                    return;
                case 25:
                    this.acceptLanguage = value;
                    return;
                case 26:
                    this.timestamp = Integer.parseInt(value);
                    return;
                case 27:
                    this.dynamicVariablePrefix = value;
                    return;
                case 28:
                    this.visitorID = value;
                    return;
                case 29:
                    this.vmk = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_XBOXGAMEVIDEO /*30*/:
                    this.visitorMigrationKey = value;
                    return;
                case 31:
                    this.visitorMigrationServer = value;
                    return;
                case 32:
                    this.visitorMigrationServerSecure = value;
                    return;
                case 33:
                    this.charSet = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_XBOXGAMETRAILER /*34*/:
                    this.visitorNamespace = value;
                    return;
                case 35:
                    this.cookieDomainPeriods = Integer.parseInt(value);
                    return;
                case EDSV2MediaType.MEDIATYPE_XBOXBUNDLE /*36*/:
                    this.cookieLifetime = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_XBOXXNACOMMUNITYGAME /*37*/:
                    this.pageName = value;
                    return;
                case 38:
                    this.pageURL = value;
                    return;
                case 39:
                    this.referrer = value;
                    return;
                case YouProfileModel.YOU_PROFILE_SECTIONS /*41*/:
                    this.currencyCode = value;
                    return;
                case 42:
                    this.lightProfileID = value;
                    return;
                case 43:
                    this.lightStoreForSeconds = Integer.parseInt(value);
                    return;
                case 44:
                    this.lightIncrementBy = Integer.parseInt(value);
                    return;
                case 45:
                    this.retrieveLightProfiles = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_XBOXMARKETPLACE /*46*/:
                    this.deleteLightProfiles = value;
                    return;
                case 48:
                    this.purchaseID = value;
                    return;
                case 49:
                    this.variableProvider = value;
                    return;
                case 50:
                    this.channel = value;
                    return;
                case 51:
                    this.server = value;
                    return;
                case 52:
                    this.pageType = value;
                    return;
                case 53:
                    this.transactionID = value;
                    return;
                case 54:
                    this.campaign = value;
                    return;
                case 55:
                    this.state = value;
                    return;
                case 56:
                    this.zip = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_WEBGAME /*57*/:
                    this.events = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_MOBILEGAME /*58*/:
                    this.events2 = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_XBOXMOBILEPDLC /*59*/:
                    this.products = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_XBOXMOBILECONSUMABLE /*60*/:
                    this.tnt = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_XBOXAPP /*61*/:
                    this.prop1 = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_METROGAME /*62*/:
                    this.eVar1 = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_METROGAMECONTENT /*63*/:
                    this.hier1 = value;
                    return;
                case 64:
                    this.list1 = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_GAMELAYER /*65*/:
                    this.prop2 = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_GAMEACTIVITY /*66*/:
                    this.eVar2 = value;
                    return;
                case EDSV2MediaType.MEDIATYPE_APPACTIVITY /*67*/:
                    this.hier2 = value;
                    return;
                case 68:
                    this.list2 = value;
                    return;
                case 69:
                    this.prop3 = value;
                    return;
                case 70:
                    this.eVar3 = value;
                    return;
                case 71:
                    this.hier3 = value;
                    return;
                case 72:
                    this.list3 = value;
                    return;
                case 73:
                    this.prop4 = value;
                    return;
                case 74:
                    this.eVar4 = value;
                    return;
                case 75:
                    this.hier4 = value;
                    return;
                case 76:
                    this.prop5 = value;
                    return;
                case 77:
                    this.eVar5 = value;
                    return;
                case 78:
                    this.hier5 = value;
                    return;
                case 79:
                    this.prop6 = value;
                    return;
                case 80:
                    this.eVar6 = value;
                    return;
                case 81:
                    this.prop7 = value;
                    return;
                case 82:
                    this.eVar7 = value;
                    return;
                case 83:
                    this.prop8 = value;
                    return;
                case 84:
                    this.eVar8 = value;
                    return;
                case 85:
                    this.prop9 = value;
                    return;
                case 86:
                    this.eVar9 = value;
                    return;
                case 87:
                    this.prop10 = value;
                    return;
                case 88:
                    this.eVar10 = value;
                    return;
                case 89:
                    this.prop11 = value;
                    return;
                case 90:
                    this.eVar11 = value;
                    return;
                case 91:
                    this.prop12 = value;
                    return;
                case 92:
                    this.eVar12 = value;
                    return;
                case 93:
                    this.prop13 = value;
                    return;
                case 94:
                    this.eVar13 = value;
                    return;
                case 95:
                    this.prop14 = value;
                    return;
                case 96:
                    this.eVar14 = value;
                    return;
                case MeProfileModel.ME_PROFILE_SECTIONS /*97*/:
                    this.prop15 = value;
                    return;
                case 98:
                    this.eVar15 = value;
                    return;
                case 99:
                    this.prop16 = value;
                    return;
                case 100:
                    this.eVar16 = value;
                    return;
                case 101:
                    this.prop17 = value;
                    return;
                case 102:
                    this.eVar17 = value;
                    return;
                case 103:
                    this.prop18 = value;
                    return;
                case 104:
                    this.eVar18 = value;
                    return;
                case 105:
                    this.prop19 = value;
                    return;
                case 106:
                    this.eVar19 = value;
                    return;
                case 107:
                    this.prop20 = value;
                    return;
                case 108:
                    this.eVar20 = value;
                    return;
                case 109:
                    this.prop21 = value;
                    return;
                case 110:
                    this.eVar21 = value;
                    return;
                case 111:
                    this.prop22 = value;
                    return;
                case 112:
                    this.eVar22 = value;
                    return;
                case 113:
                    this.prop23 = value;
                    return;
                case 114:
                    this.eVar23 = value;
                    return;
                case 115:
                    this.prop24 = value;
                    return;
                case 116:
                    this.eVar24 = value;
                    return;
                case 117:
                    this.prop25 = value;
                    return;
                case 118:
                    this.eVar25 = value;
                    return;
                case 119:
                    this.prop26 = value;
                    return;
                case 120:
                    this.eVar26 = value;
                    return;
                case 121:
                    this.prop27 = value;
                    return;
                case 122:
                    this.eVar27 = value;
                    return;
                case 123:
                    this.prop28 = value;
                    return;
                case 124:
                    this.eVar28 = value;
                    return;
                case 125:
                    this.prop29 = value;
                    return;
                case 126:
                    this.eVar29 = value;
                    return;
                case 127:
                    this.prop30 = value;
                    return;
                case 128:
                    this.eVar30 = value;
                    return;
                case 129:
                    this.prop31 = value;
                    return;
                case 130:
                    this.eVar31 = value;
                    return;
                case 131:
                    this.prop32 = value;
                    return;
                case 132:
                    this.eVar32 = value;
                    return;
                case 133:
                    this.prop33 = value;
                    return;
                case 134:
                    this.eVar33 = value;
                    return;
                case 135:
                    this.prop34 = value;
                    return;
                case 136:
                    this.eVar34 = value;
                    return;
                case 137:
                    this.prop35 = value;
                    return;
                case 138:
                    this.eVar35 = value;
                    return;
                case 139:
                    this.prop36 = value;
                    return;
                case 140:
                    this.eVar36 = value;
                    return;
                case 141:
                    this.prop37 = value;
                    return;
                case 142:
                    this.eVar37 = value;
                    return;
                case 143:
                    this.prop38 = value;
                    return;
                case 144:
                    this.eVar38 = value;
                    return;
                case 145:
                    this.prop39 = value;
                    return;
                case 146:
                    this.eVar39 = value;
                    return;
                case 147:
                    this.prop40 = value;
                    return;
                case 148:
                    this.eVar40 = value;
                    return;
                case 149:
                    this.prop41 = value;
                    return;
                case 150:
                    this.eVar41 = value;
                    return;
                case 151:
                    this.prop42 = value;
                    return;
                case 152:
                    this.eVar42 = value;
                    return;
                case 153:
                    this.prop43 = value;
                    return;
                case 154:
                    this.eVar43 = value;
                    return;
                case 155:
                    this.prop44 = value;
                    return;
                case 156:
                    this.eVar44 = value;
                    return;
                case 157:
                    this.prop45 = value;
                    return;
                case 158:
                    this.eVar45 = value;
                    return;
                case 159:
                    this.prop46 = value;
                    return;
                case 160:
                    this.eVar46 = value;
                    return;
                case 161:
                    this.prop47 = value;
                    return;
                case 162:
                    this.eVar47 = value;
                    return;
                case 163:
                    this.prop48 = value;
                    return;
                case 164:
                    this.eVar48 = value;
                    return;
                case 165:
                    this.prop49 = value;
                    return;
                case 166:
                    this.eVar49 = value;
                    return;
                case 167:
                    this.prop50 = value;
                    return;
                case 168:
                    this.eVar50 = value;
                    return;
                case 169:
                    this.prop51 = value;
                    return;
                case 170:
                    this.eVar51 = value;
                    return;
                case 171:
                    this.prop52 = value;
                    return;
                case 172:
                    this.eVar52 = value;
                    return;
                case 173:
                    this.prop53 = value;
                    return;
                case 174:
                    this.eVar53 = value;
                    return;
                case 175:
                    this.prop54 = value;
                    return;
                case 176:
                    this.eVar54 = value;
                    return;
                case 177:
                    this.prop55 = value;
                    return;
                case 178:
                    this.eVar55 = value;
                    return;
                case 179:
                    this.prop56 = value;
                    return;
                case 180:
                    this.eVar56 = value;
                    return;
                case 181:
                    this.prop57 = value;
                    return;
                case 182:
                    this.eVar57 = value;
                    return;
                case 183:
                    this.prop58 = value;
                    return;
                case 184:
                    this.eVar58 = value;
                    return;
                case 185:
                    this.prop59 = value;
                    return;
                case 186:
                    this.eVar59 = value;
                    return;
                case 187:
                    this.prop60 = value;
                    return;
                case 188:
                    this.eVar60 = value;
                    return;
                case 189:
                    this.prop61 = value;
                    return;
                case 190:
                    this.eVar61 = value;
                    return;
                case 191:
                    this.prop62 = value;
                    return;
                case 192:
                    this.eVar62 = value;
                    return;
                case 193:
                    this.prop63 = value;
                    return;
                case 194:
                    this.eVar63 = value;
                    return;
                case 195:
                    this.prop64 = value;
                    return;
                case 196:
                    this.eVar64 = value;
                    return;
                case 197:
                    this.prop65 = value;
                    return;
                case 198:
                    this.eVar65 = value;
                    return;
                case 199:
                    this.prop66 = value;
                    return;
                case 200:
                    this.eVar66 = value;
                    return;
                case 201:
                    this.prop67 = value;
                    return;
                case 202:
                    this.eVar67 = value;
                    return;
                case 203:
                    this.prop68 = value;
                    return;
                case 204:
                    this.eVar68 = value;
                    return;
                case 205:
                    this.prop69 = value;
                    return;
                case 206:
                    this.eVar69 = value;
                    return;
                case 207:
                    this.prop70 = value;
                    return;
                case 208:
                    this.eVar70 = value;
                    return;
                case 209:
                    this.prop71 = value;
                    return;
                case 210:
                    this.eVar71 = value;
                    return;
                case 211:
                    this.prop72 = value;
                    return;
                case 212:
                    this.eVar72 = value;
                    return;
                case 213:
                    this.prop73 = value;
                    return;
                case 214:
                    this.eVar73 = value;
                    return;
                case 215:
                    this.prop74 = value;
                    return;
                case 216:
                    this.eVar74 = value;
                    return;
                case 217:
                    this.prop75 = value;
                    return;
                case 218:
                    this.eVar75 = value;
                    return;
                case 219:
                    this.pe = value;
                    return;
                case 220:
                    this.pev1 = value;
                    return;
                case 221:
                    this.pev2 = value;
                    return;
                case 222:
                    this.pev3 = value;
                    return;
                case 223:
                    this.resolution = value;
                    return;
                default:
                    return;
            }
        } catch (Exception e) {
        }
    }
}
