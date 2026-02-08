import ballerina/data.xmldata;

@xmldata:Namespace {uri: "http://www.opentravel.org/OTA/2003/05"}
public type NotificationRequest record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
    @xmldata:Attribute
    boolean GuaranteePolicy?;
    @xmldata:Attribute
    boolean CancellationPolicy?;
    @xmldata:Attribute
    boolean RateSeason?;
    @xmldata:Name {value: "StayRestrictions"}
    @xmldata:Attribute
    boolean StayRestrictions1?;
|};

@xmldata:Namespace {uri: "http://www.opentravel.org/OTA/2003/05"}
public type ManagedInCrsOnly record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
    @xmldata:Attribute
    boolean GuaranteePolicy?;
    @xmldata:Attribute
    boolean CancellationPolicy?;
    @xmldata:Attribute
    boolean RateSeason?;
    @xmldata:Name {value: "StayRestrictions"}
    @xmldata:Attribute
    boolean StayRestrictions1?;
|};

@xmldata:Namespace {uri: "http://www.opentravel.org/OTA/2003/05"}
public type ManagedStayRestrictionTypes record {|
    @xmldata:Attribute
    boolean Closed?;
    @xmldata:Attribute
    boolean NoArrival?;
    @xmldata:Attribute
    boolean NoDeparture?;
    @xmldata:Attribute
    boolean NoSell?;
|};

@xmldata:Namespace {uri: "http://www.opentravel.org/OTA/2003/05"}
public type SequenceGroup record {|
    @xmldata:Namespace {uri: "http://www.opentravel.org/OTA/2003/05"}
    @xmldata:SequenceOrder {value: 1}
    ManagedStayRestrictionTypes StayRestrictions?;
|};
