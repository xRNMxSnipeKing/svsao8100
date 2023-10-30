package com.microsoft.xbox.service.model.zest;

import com.microsoft.xbox.service.model.serialization.UTCDateConverter;
import java.util.Date;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

@Root(name = "Balances")
public class Balances {
    @Element
    public int PointsBalance;
    @Element
    public int SongCreditBalance;
    @Element
    @Convert(UTCDateConverter.class)
    public Date SongCreditRenewalDate;
}
