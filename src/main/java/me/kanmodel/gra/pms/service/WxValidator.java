package me.kanmodel.gra.pms.service;

import me.kanmodel.gra.pms.wx.dao.WxUserRepository;
import me.kanmodel.gra.pms.wx.entity.WxUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class WxValidator {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private WxUserRepository wxUserRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static Long EXPIRED = (long) (30 * 60 * 1000);

    public boolean valid(String openid, String sig) {
        StackTraceElement[] temp = Thread.currentThread().getStackTrace();
        StackTraceElement parent = temp[2];
        String call = parent.getFileName() + "-" + parent.getMethodName();

        //判断是否参数完整
        if (sig == null || openid == null) {
            logger.warn(call + ":验证缺少参数！");
            return false;
        }

        WxUser wxUser = wxUserRepository.findByOpenid(openid);
        //判断用户是否存在
        if (wxUser == null) {
            logger.warn(call + ":用户[" + openid + "] 不存在");
            return false;
        }
        //校验密钥sig是否匹配
        if (!bCryptPasswordEncoder.matches(wxUser.getSessionKey(), sig)) {
            logger.warn(call + ":用户[" + openid + "] sig验证失败");
            logger.warn(call + ":sig[" + sig + "] sig验证失败");
            return false;
        }

        //检验密钥sig是否过期
        if (wxUser.getModified().getTime() + EXPIRED < System.currentTimeMillis()) {
            logger.warn(call + ":用户[" + openid + "] sig过期! modified:" + wxUser.getModified().getTime());
            return false;
        }

        return true;
    }
}
