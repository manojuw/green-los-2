package com.mixo.test;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Head {

    @XmlAttribute(name = "msgId")
    private String msgId;

    @XmlAttribute(name = "orgId")
    private String orgId;

    @XmlAttribute(name = "prodType")
    private String prodType;

    @XmlAttribute(name = "ts")
    private String timestamp;

    @XmlAttribute(name = "ver")
    private String version;
}
