package cn.ccut.ylp.fun;

import cn.ccut.ylp.*;
import cn.ccut.ylp.date.AutoConvertDate;

import java.util.ArrayList;

/**
 * 对账要素转换，主要将实体转化为对账要素。
 */
public class ElementServices {
    public Element convert(AbcEntity entity){
        Element e = new Element();
        e.datetime = AutoConvertDate.convert(entity.datetime);
        e.amount = entity.amount;
        e.bank = "abc";
        e.important = entity.counterpartyName;
        return e;
    }
    public Element convert(CmbEntity entity){
        Element e = new Element();
        e.datetime = AutoConvertDate.convert(entity.datetime);
        if (entity.income != null){
            e.amount = entity.income;
        } else {
            // 支出取负
            e.amount = entity.expenditure.negate();
        }

        e.bank = "cmb";
        e.important = entity.transactionNotes;
        return e;
    }
    public Element convert(CmbPdfEntity entity){
        Element e = new Element();
        e.datetime = AutoConvertDate.convert(entity.date);
        e.amount = entity.amount;
        e.bank = "cmb_pdf";
        e.important = entity.counterParty;
        return e;
    }
    public Element convert(AlipayEntity entity){
        Element e = new Element();
        e.datetime = AutoConvertDate.convert(entity.createDate);
        if (entity.ieType.equals("收入")){
            e.amount = entity.amount;
        } else {
            e.amount = entity.amount.negate();
        }
        e.bank = "alipay";
        e.important = entity.counterpartyName;
        return e;
    }
    public Element convert(WechatpayEntity entity){
        Element e = new Element();
        e.datetime = AutoConvertDate.convert(entity.datetime);
        if (entity.ieType.equals("收入")){
            e.amount = entity.amount;
        } else {
            e.amount = entity.amount.negate();
        }
        e.bank = "wechatpay";
        e.important = entity.counterpartyName;
        return e;
    }
    public ArrayList<Element> convertAbc(ArrayList<AbcEntity> lists){
        ArrayList<Element> elements = new ArrayList<Element>();
        for (AbcEntity entity :lists){
            Element element = convert(entity);
            elements.add(element);
        }
        return elements;
    }
    public ArrayList<Element> convertCmb(ArrayList<CmbEntity> lists){
        ArrayList<Element> elements = new ArrayList<Element>();
        for (CmbEntity entity :lists){
            Element element = convert(entity);
            elements.add(element);
        }
        return elements;
    }
    public ArrayList<Element> convertCmbPdf(ArrayList<CmbPdfEntity> lists){
        ArrayList<Element> elements = new ArrayList<Element>();
        for (CmbPdfEntity entity :lists){
            Element element = convert(entity);
            elements.add(element);
        }
        return elements;
    }
    public ArrayList<Element> convertAlipay(ArrayList<AlipayEntity> lists){
        ArrayList<Element> elements = new ArrayList<Element>();
        for (AlipayEntity entity :lists){
            Element element = convert(entity);
            elements.add(element);
        }
        return elements;
    }
    public ArrayList<Element> convertWechatpay(ArrayList<WechatpayEntity> lists){
        ArrayList<Element> elements = new ArrayList<Element>();
        for (WechatpayEntity entity :lists){
            Element element = convert(entity);
            elements.add(element);
        }
        return elements;
    }

}
