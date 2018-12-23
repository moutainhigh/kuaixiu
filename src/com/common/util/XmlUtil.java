package com.common.util;


import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class XmlUtil {

    private static final Logger log=Logger.getLogger(XmlUtil.class);

    public Map<String,Object> map = new HashMap<String,Object>();

    /**
     * 解析xml 转换为Map
     * @param soap
     * @return
     * @throws DocumentException
     */
    public  Map parse(String soap) throws DocumentException {
        Document doc = DocumentHelper.parseText(soap);//报文转成doc对象
        Element root = doc.getRootElement();//获取根元素，准备递归解析这个XML树
        try {
            getCode(root);
        } catch (Exception e) {
            log.info("解析webservice xml失败");
            e.printStackTrace();
        }
        return map;
    }

    public  void getCode(Element root){
        if(root.elements()!=null){
            List<Element> list = root.elements();//如果当前跟节点有子节点，找到子节点
            for(Element e:list){//遍历每个节点
                if(e.elements().size()>0){
                    getCode(e);//当前节点不为空的话，递归遍历子节点；
                }
                if(e.elements().size()==0){
                    map.put(e.getName(), e.getTextTrim());
                }//如果为叶子节点，那么直接把名字和值放入map
                System.out.println(e.getName()+"  "+e.getStringValue());
            }
        }
    }

    public static void main(String[]args) throws DocumentException {
        //String soap = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:s=\"http://esb.spdbbiz.com/services/S120030432\" xmlns:d=\"http://esb.spdbbiz.com/metadata\" ><soap:Header><s:ReqHeader><d:Mac>0000000000000000</d:Mac><d:MacOrgId></d:MacOrgId><d:SourceSysId>0825</d:SourceSysId><d:ConsumerId>0825</d:ConsumerId><d:ServiceAdr>http://esb.spdbbiz.com:7701/services/S120030432</d:ServiceAdr><d:ServiceAction>urn:/UnfSocCrdtNoQry</d:ServiceAction><d:ExtendContent>00</d:ExtendContent></s:ReqHeader></soap:Header><soap:Body><s:ReqUnfSocCrdtNoQry><s:ReqSvcHeader><s:TranDate>20170913</s:TranDate><s:TranTime>162116290</s:TranTime><s:TranTellerNo>00000000</s:TranTellerNo><s:TranSeqNo></s:TranSeqNo><s:ConsumerId>0825</s:ConsumerId><s:GlobalSeqNo>15800001016431580643158064</s:GlobalSeqNo><s:SourceSysId>0825</s:SourceSysId><s:BranchId></s:BranchId><s:TerminalCode>1a</s:TerminalCode><s:CityCode>021</s:CityCode><s:AuthrTellerNo></s:AuthrTellerNo><s:AuthrPwd></s:AuthrPwd><s:AuthrCardFlag></s:AuthrCardFlag><s:AuthrCardNo></s:AuthrCardNo><s:TranCode>DM91</s:TranCode><s:PIN>1234567890123</s:PIN><s:SysOffset1>0000</s:SysOffset1><s:SysOffset2>0000</s:SysOffset2><s:MsgEndFlag>1</s:MsgEndFlag><s:MsgSeqNo>3000</s:MsgSeqNo><s:SubTranCode></s:SubTranCode><s:TranMode></s:TranMode><s:TranSerialNo>0</s:TranSerialNo></s:ReqSvcHeader><s:SvcBody><s:OrgInstId>100000024</s:OrgInstId></s:SvcBody></s:ReqUnfSocCrdtNoQry></soap:Body></soap:Envelope> ";
        //失败
        String soap="<?xml version=\"1.0\"?>\n" +
                 "<soap:Envelope\n" +
                 "    xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                 "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                 "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                 "    <soap:Body>\n" +
                 "        <ns1:accountInfoQueryResponse\n" +
                 "            xmlns:ns1=\"http://www.chinatelecom.hub.com\">\n" +
                 "            <ns1:response>\n" +
                 "                <![CDATA[<CAPRoot><SessionHeader><TransactionID>13000201805183044198370</TransactionID><ActionCode>1</ActionCode><RspTime>20180822070820</RspTime><Response><RspType>1</RspType><RspCode>1992</RspCode><RspDesc>Ticket已超时</RspDesc></Response></SessionHeader></CAPRoot>]]>\n" +
                 "            </ns1:response>\n" +
                 "        </ns1:accountInfoQueryResponse>\n" +
                 "    </soap:Body>\n" +
                 "</soap:Envelope>";

         //成功
         String soap1="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                 "\n" +
                 "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">  \n" +
                 "  <soap:Body> \n" +
                 "    <ns1:accountInfoQueryResponse xmlns:ns1=\"http://www.chinatelecom.hub.com\">  \n" +
                 "      <ns1:response> <![CDATA[<CAPRoot><SessionHeader><TransactionID>13000201805183044198370</TransactionID><ActionCode>1</ActionCode><RspTime>20180813032539</RspTime><Response><RspType>0</RspType><RspCode>0000</RspCode><RspDesc>Success</RspDesc></Response></SessionHeader><SessionBody><AssertionQueryResp><AccountType>2000004</AccountType><AccountID>18106538281</AccountID><PWDType>03</PWDType><ProvinceID>12</ProvinceID><ReturnURL>http://www.189.cn/tymh/address_plus.do</ReturnURL><PWDAttrList/><TrustedAccList/></AssertionQueryResp></SessionBody></CAPRoot>]]> </ns1:response> \n" +
                 "    </ns1:accountInfoQueryResponse> \n" +
                 "  </soap:Body> \n" +
                 "</soap:Envelope>";
        //初始化报文，调用parse方法，获得结果map，然后按照需求取得字段，或者转化为其他格式
        Map map = new XmlUtil().parse(soap1);
        //获得字段s:SourceSysId的值;
        String id = map.get("response").toString();
        System.out.println("response主体"+id);
        Map parse = new XmlUtil().parse(id);
        String type=parse.get("RspType").toString();
        System.out.println("转换类型："+type);
        if(type.equals("0")){

        }
        System.out.println("流水号："+parse.get("TransactionID").toString());
        System.out.println("手机号: "+parse.get("AccountID").toString());

    }
}