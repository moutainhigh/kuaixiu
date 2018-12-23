package com.common.exception;

/**
 * <p>
 * 为便于对异常进行统一处理，系统中定义一个异常基类。
 * </p>
 * <p>
 * 对于自己在<code>try(...)catch(){...}</code>catch块中捕获的异常，可以直接再自已构造一个EshopException抛出，
 * 系统将自动转到错误页面，并将错误信息显示在错误页面中。使用方法如下：
 * <pre>
 * try{
 *      ...
 * }
 * catch(***Exception e)
 * {
 *         throw new SystemException("错误提示");
 * }
 * </pre>
 * 或者使用带有返回地址的构造函数：如下
 * try{
 *         ...
 * }
 * catch(***Exception e)
 * {
 *         throw new SystemException("错误提示","返回url地址");
 * }
 * </pre>
 * 在返回地址参数中传入要引导用户要返回的页面地址。
 * </p>
 * 
 * <p>
 * 如果系统默认的错误处理页面不能满足要求，也可以自定异常类型，继承<code>SystemException</code>,
 * 然后在applicationContext.xml中的<code><property name="exceptionMappings"> props</code>属性中做相应异常类型的配置
 * <pre>
 *    <!-- 统一异常处理 -->
 *    <bean id="exceptionResolver" class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
 *        <property name="defaultErrorView">
 *            <value>common/defaultException</value>
 *        </property>
 *        <property name="exceptionMappings">
 *            <props>
 *                <prop key="java.sql.SQLException">common/defaultException</prop>
 *                <prop key="com.commons.exception.SystemException">common/systemException</prop>
 *            </props>
 *        </property>
 *    </bean>
 * </pre>
 * </p>
 */
public class SystemException extends RuntimeException {
    
    /**
     * 序列化id
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 保存出错时用户操作的url地址，方便用户返回刚才的操作页面
     */
    private String backUrl=null;
    
    /**
     * @param msg 出错提示信息
     * @see java.lang.Exception
     */
    public SystemException(String msg){
        super(msg);
        System.out.println(msg);
    }
    
    /**
     * @param msg 出错提示信息
     * @param backUrl 可以传入出错时操作的url地址，方便用户返回刚才的操作页面,或者错误返回码提示
     */
    public SystemException(String msg,String backUrl){
        super(msg);
        this.backUrl = backUrl;
        System.out.println(msg);
    }
    
    /**
     * 返回回调路径
     * @return
     * @author: lijx
     * @CreateDate: 2016-6-3 上午11:16:18
     */
    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    /**
     * 返回异常消息
     */
    public String toString(){
        return getMessage();
    }
}
