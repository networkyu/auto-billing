package cn.ccut.ylp.fun;

import cn.ccut.ylp.*;
import cn.ccut.ylp.date.AutoConvertDate;
import com.mysql.cj.exceptions.DeadlockTimeoutRollbackMarker;
import com.mysql.cj.xdevapi.SqlDataResult;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.plugin.javascript.navig.Array;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * v1.1 本类实现从某个日期到某个日期，我给某个人转了多少钱，某个人给我转了多少钱，
 * 两者之间的差值。
 * v1.2 有一种特殊情况是，我将钱转给A，然后A转给B，B在转给我，那么我统计B给我多少钱时，
 * 这一笔事实上是我的资金的交易也需要筛选出来进行区别对待。
 */
public class Reconciliation {
    public Date start;
    public Date end;
    /*初始化信息*/
    public String formart = "yyyy-MM-dd";
    public String formartExact = "yyyy-MM-dd HH:mm:ss";
    public String[] abcCounterpartlyNames = {"%全名%"};
    //public String[] abcCounterpartlyAccounts = {"6228483646153618068"};
    public String[] cmbNotes = {"%全名%"};
    public String[] cmbExcludeNotes = {"%支付宝%"};
    public String[] alipayCounterpartlyNames = {"%部分名称%","%店铺名称%"};
    public String[] wechatpayCounterpartlyNames = {"%备注名称%","%店铺名称%"};
    public String[] cmbPdfCounterParty = {"%全名%"};
    /*各个银行失败交易对方字段条件*/
    public String abcNameConditions = "";
//    public String abcAccountConditions = "";
    public String cmbNoteConditions = "";
    public String cmbExcludeConditions = "";
    public String alipayNameConditions = "";
    public String wechatpayNameConditions = "";
    public String cmbPdfConditions = "";
    public AbcDB abcDB = new AbcDB();
    public AlipayDB alipayDb = new AlipayDB();
    public CmbDB cmbDb = new CmbDB();
    public WechatpayDB wechatpayDB = new WechatpayDB();
    public CmbPdfDB cmbPdfDB = new CmbPdfDB();
    ConnectionDB con = null;
    //累计利息默认日利率
    private BigDecimal rate = new BigDecimal("0.0006");
    protected static final Logger logger = LogManager.getLogger("mylog");
    // 区间查询。
    public Reconciliation(Date startD,Date endD){
        initCondition();
        start = startD;
        end = endD;
    }
    //默认全部数据查询。
    public Reconciliation(){
        initCondition();
    }
    public void initCondition(){
        //abcAccountConditions = spliceCondition(abcCounterpartlyAccounts,"counterparty_account","=");
        abcNameConditions = spliceCondition(abcCounterpartlyNames,"counterparty_name"," like ");
        cmbNoteConditions = spliceCondition(cmbNotes,"transaction_notes"," like");
        cmbExcludeConditions = spliceCondition(cmbExcludeNotes,"transaction_notes"," not like");
        alipayNameConditions = spliceCondition(alipayCounterpartlyNames,"counterparty_name", " like ");
        wechatpayNameConditions = spliceCondition(wechatpayCounterpartlyNames,"counterparty_name", " like ");
        cmbPdfConditions = spliceCondition(cmbPdfCounterParty,"counter_party", " like ");
    }
    /**
     * 拼接条件字符串，宁多不少，两者之间用or
     * @param data 拼接所用数据
     * @param colName 列名
     */
    public String spliceCondition(String[] data,String colName,String relation){
        if(data != null && data.length>0){
            StringBuilder conditions = new StringBuilder();
            for (int i = 0;i<data.length;i++){
                String temp = colName+ relation+"'" + data[i] + "' or ";
                conditions.append(temp);
            }
            //取掉最后的or。
            conditions = conditions.delete(conditions.length() - 3,conditions.length());
            if (data.length>1){
                //多个条件
                conditions.insert(0,"(");
                conditions.append(" )");
            }
            return conditions.toString();
        }
        return "";
    }
    public boolean loadCalInterval(String table){
        String dateColName = "datetime";
        if (table.equals("alipay")) {
            dateColName = "create_date";
        }
        if(table.equals("cmb_pdf")){
            dateColName = "date";
        }
        String sql1 = "select "+dateColName+" from " + table + " order by "+dateColName+" limit 0,1";
        String sql2 = "select "+dateColName+" from " + table + " order by "+dateColName+" desc limit 0,1";
        start = loadDate(sql1);
        end = loadDate(sql2);
        if (start != null){
            return true;
        }
        return false;
    }
    public Date loadDate(String sql) {
        try {
            ResultSet rs = null;
            rs = con.exe(sql);
            while (rs.next()){
                // 列从1开始。
                return AutoConvertDate.convert(rs.getString(1));
            }
        } catch(SQLException ex1){
            System.out.println("查询日期sql异常");
        }
        return null;
    }
    public void run(){
        // 从每一天推。
        Date tempDate = start;
        // 初始化数据库连接
        if(con==null){
            con = new ConnectionDB();
        }
        System.out.println("开始时间："+ DateFormatUtils.format(new Date(),"yyyyMMdd HHmmss"));
        ArrayList<AbcEntity> abcEntities = new ArrayList<AbcEntity>();
        ArrayList<CmbEntity> cmbEntities = new ArrayList<CmbEntity>();
        ArrayList<CmbPdfEntity> cmbPdfEntities = new ArrayList<CmbPdfEntity>();
        ArrayList<AlipayEntity> alipayEntities = new ArrayList<AlipayEntity>();
        ArrayList<WechatpayEntity> wechatpayEntities = new ArrayList<WechatpayEntity>();
        String[] abcC = {abcNameConditions};
        String[] cmbC = {cmbNoteConditions,cmbExcludeConditions};
        String[] alipayC = {alipayNameConditions};
        String[] wechatpayC = {wechatpayNameConditions};
        String[] cmbPdfC = {cmbPdfConditions};
        String[] tableNames = {"abc","cmb","alipay","wechatpay","cmb_pdf"};
        if ( loadCalInterval(tableNames[0])){
            // 区间已加载执行查询语句。
            abcEntities = abcDB.readFromResultSet(search(tableNames[0],abcC));
        }
        if ( loadCalInterval(tableNames[1])){
            // 区间已加载执行查询语句。
            cmbEntities = cmbDb.readFromResultSet(search(tableNames[1],cmbC));
        }
        if ( loadCalInterval(tableNames[2])){
            // 区间已加载执行查询语句。
            alipayEntities = alipayDb.readFromResultSet(search(tableNames[2],alipayC));
        }
        if ( loadCalInterval(tableNames[3])){
            // 区间已加载执行查询语句。
            wechatpayEntities = wechatpayDB.readFromResultSet(search(tableNames[3],wechatpayC));
        }
        //加载pdf账单-招商银行
        if ( loadCalInterval(tableNames[4])){
            // 区间已加载执行查询语句。
            cmbPdfEntities = cmbPdfDB.readFromResultSet(search(tableNames[4],cmbPdfC));
        }
        ElementServices services = new ElementServices();
        ArrayList<Element> abcEs = services.convertAbc(abcEntities);
        ArrayList<Element> cmbEs = services.convertCmb(cmbEntities);
        ArrayList<Element> cmbPdfEs = services.convertCmbPdf(cmbPdfEntities);
        ArrayList<Element> alipayEs = services.convertAlipay(alipayEntities);
        ArrayList<Element> wechatpayEs = services.convertWechatpay(wechatpayEntities);
        // 合并
        abcEs.addAll(cmbEs);
        abcEs.addAll(alipayEs);
        abcEs.addAll(wechatpayEs);
        abcEs.addAll(cmbPdfEs);
//        for (int i = 0; i < abcEs.size(); i++) {
//            Element e = abcEs.get(i);
//            e.toString();
//        }
        // 排序
        Collections.sort(abcEs, new Comparator<Element>() {
            @Override
            public int compare(Element o1, Element o2) {
//                正，零，负，大于，等于，小于
                if(o1.datetime.before(o2.datetime)){
                    return 1;
                }
                if (o1.datetime.after(o2.datetime)){
                    return -1;
                }
                return 0;
            }
        });
        Collections.reverse(abcEs);
        // 处理。
        work(abcEs);
        if(con != null){
            con.close();
        }
        System.out.println("结束时间："+ DateFormatUtils.format(new Date(),"yyyyMMdd HHmmss"));
    }
    // 查询当天的交易，通过每天去查询sql进行处理，这样效率比较低 v1.1.1,通过时间区间一次性全部查出来。v1.1.2.
    public ResultSet search(String tableName, String[] conditions){
        StringBuilder s = new StringBuilder("SELECT * FROM ");
        s.append(tableName);
        s.append(" where ");
        String colName = "datetime";
        switch (tableName){
            case "alipay" :
                colName = "create_date";
                break;
            case "cmb_pdf":
                colName = "date";
                break;
        }
        s.append(colName);
        s.append(" between  ?  and ? ");
        for(int i=0;i<conditions.length;i++){
            if (!conditions[i].equals("")){
                s.append("and ");
                s.append(conditions[i]);
            }
        }
        s.append(" order by ");
        s.append(colName);
        String[] d = {DateFormatUtils.format(start,formartExact),
                DateFormatUtils.format(end,formartExact)};
        return con.exe(s.toString(),d,null,null);
    }
    //传入符合条件的银行账单数据
    public void work(ArrayList<Element> elements){
        if (elements.size() <=0){
            //如果无数据，则直接返回即可。
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // 累计本金。
        BigDecimal principal = new BigDecimal(0);
        //累计借出
        BigDecimal out = BigDecimal.ZERO;
        //累计还款
        BigDecimal in = BigDecimal.ZERO;
        // 累计利息
        BigDecimal interest = new BigDecimal(0);
        // 累计收入。
        BigDecimal earnings = BigDecimal.ZERO;
        //30天计息通道。
        ArrayList<BigDecimal> interestAisle = new ArrayList<BigDecimal>();
        //当天日期
        Calendar tempC = AutoConvertDate.setTimeZero(elements.get(0).datetime);
        Calendar endC = AutoConvertDate.setTimeZero(elements.get(elements.size() -1 ).datetime);
        int index = 0;// 遍历数据指针
        HashMap<String,String> checkInfo = new HashMap<>();
        while (!tempC.after(endC)){
            //累计本金一天的利息
            checkInfo.put("date",simpleDateFormat.format(tempC.getTime()));
            checkInfo.put("lastPrincpal",formartBigDecimal(principal));
            BigDecimal principalInterest = principal.multiply(rate);
            checkInfo.put("princpalInterest",formartBigDecimal(principalInterest));
            BigDecimal income = new BigDecimal(0);
            BigDecimal expenditure = new BigDecimal(0);
            //当天的所有交易。
            while (elements.size() > index && DateUtils.isSameDay(elements.get(index).datetime,tempC)){
                //如果一天既存在正值，又存在负值,需分别计算，否则，凌晨借1万，23点还1万，每天都借，但利息却永远是零。
                Element element = elements.get(index);
                if (element.amount.compareTo(BigDecimal.ZERO) > 0){
                    //大于零为收入。
                    income = income.add(element.amount);
                } else {
                    expenditure = expenditure.add(element.amount);
                }
                //索引向后移动。
                index = index + 1;
            }
            checkInfo.put("income",formartBigDecimal(income));
            checkInfo.put("expenditure",formartBigDecimal(expenditure));
            out = out.add(expenditure);
            in = in.add(income);
            BigDecimal todayInterest = BigDecimal.ZERO;
            //支出的负值需要计息，而正值为还款！
            if(expenditure.compareTo(BigDecimal.ZERO) < 0){
                //        计算当天借出的利息
                todayInterest = expenditure.multiply(rate);
                //将借出资金加入到本金当中
                principal = principal.add(expenditure);
            }
            checkInfo.put("todayInterest",formartBigDecimal(todayInterest));
            // 今天的总利息。
            BigDecimal totalInterest = principalInterest.add(todayInterest);
            checkInfo.put("totalInterest",formartBigDecimal(totalInterest));
            // 利息通道加入今天产生的利息。
            interestAisle.add(totalInterest);
            checkInfo.put("aisleSize",String.valueOf(interestAisle.size()));
            BigDecimal totalAisle = BigDecimal.ZERO;
            for (int k = 0; k < interestAisle.size(); k++) {
                totalAisle =totalAisle.add(interestAisle.get(k));
            }
            checkInfo.put("totalAisle",formartBigDecimal(totalAisle));
            //累计收入利息增加！
            interest = interest.add(totalInterest);
            if(income.compareTo(BigDecimal.ZERO) > 0){
                // 收入为正，需要还款，超过30天的利息需要计算复利
                // 先还第一天的利息
                for(int i=0;i<interestAisle.size();i++){
                    //最早的利息。
                    BigDecimal firstI = interestAisle.get(i);
                    if(income.add(firstI).compareTo(BigDecimal.ZERO) >= 0){
                        // 还款有剩余。
                        income = income.add(firstI);
                        interestAisle.remove(i);
                        //如果减掉一天的利息，则再从第一个元素开始。
                        i  = i - 1;
                    } else {
                        firstI = firstI.add(income);
                        //不够还的时候，利息减掉还款，则将还款置为零。
                        income = BigDecimal.ZERO;
                        interestAisle.set(i,firstI);
                        break;//结束
                    }
                }
                //说明还有还款，则还本金。
                if(income.compareTo(BigDecimal.ZERO) > 0){
                    if(income.add(principal).compareTo(BigDecimal.ZERO) < 0){
                        principal = principal.add(income);
                    } else {
                        // 本金已经还完，累计收入。
                        earnings = earnings.add(principal.add(income));
                        //本金已还完，
                        principal = BigDecimal.ZERO;
                        System.out.println("本金已经还完了");
                    }
                }
                if (interestAisle.size() > 30){
                    // 利息增加为本金，收复利。
                    principal = principal.add(interestAisle.get(0));
                    interestAisle.remove(0);
                }
            }
            // 加一天。
            tempC.add(Calendar.DAY_OF_MONTH,1);
            // 今天***剩余本金***元，，借款***元，还款***元，本金利息**元，借款利息**元
            System.out.println("今天"+checkInfo.get("date")+"|剩余本金:"+checkInfo.get("lastPrincpal")+
                    "|借款:"+checkInfo.get("expenditure")+"|还款:"+checkInfo.get("income")+
                    "|本金利息:"+checkInfo.get("princpalInterest")+"|今天借款利息:"+checkInfo.get("todayInterest")
                    +"|今日利息和："+checkInfo.get("totalInterest")+"|通道利息数目:"+checkInfo.get("aisleSize")
                    +"|通道利息和："+checkInfo.get("totalAisle"));
        }
        System.out.println("剩余本金："+principal.setScale(2,BigDecimal.ROUND_HALF_UP));
        System.out.println("累计借出:"+out.setScale(2,BigDecimal.ROUND_HALF_UP));
        System.out.println("累计还款:"+in.setScale(2,BigDecimal.ROUND_HALF_UP));
        System.out.println("累计利息："+interest.setScale(2,BigDecimal.ROUND_HALF_UP));
        System.out.println("利润："+earnings.setScale(2,BigDecimal.ROUND_HALF_UP));
//        通道利息
        BigDecimal aisleI = BigDecimal.ZERO;
        for(BigDecimal i : interestAisle){
            aisleI = aisleI.add(i);
        }
        System.out.printf("通道剩余利息："+aisleI.setScale(2,BigDecimal.ROUND_HALF_UP));
    }
    public String formartBigDecimal(BigDecimal b){
        DecimalFormat d = new DecimalFormat("0.00");
        return d.format(b);
    }
    public static void main(String[] args) {
        Reconciliation r = new Reconciliation();
        r.run();
    }
}
