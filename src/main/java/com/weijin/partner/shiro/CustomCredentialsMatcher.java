package com.weijin.partner.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义校验密码类
 * 
 * @author yindw
 * @date 2017年8月8日
 */
public class CustomCredentialsMatcher extends SimpleCredentialsMatcher {

	private static Logger LOGGER = LoggerFactory.getLogger(CustomCredentialsMatcher.class);
	private String retryLimitCacheName;
	private final static String DEFAULT_CHACHE_NAME = "retryLimitCache";

	private Cache<String, AtomicInteger> passwordRetryCache;

	public CustomCredentialsMatcher(CacheManager cacheManager) {
		this.retryLimitCacheName = DEFAULT_CHACHE_NAME;
		this.passwordRetryCache = cacheManager.getCache(retryLimitCacheName);
	}

	public String getRetryLimitCacheName() {
		return retryLimitCacheName;
	}

	public void setRetryLimitCacheName(String retryLimitCacheName) {
		this.retryLimitCacheName = retryLimitCacheName;
	}

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		UsernamePasswordToken utoken = (UsernamePasswordToken) token;
		String username = utoken.getUsername();
		AtomicInteger retryCount = passwordRetryCache.get(username);
		if (retryCount == null) {
			retryCount = new AtomicInteger(0);
			passwordRetryCache.put(username, retryCount);
		}
		if (retryCount.incrementAndGet() > 5) {
			LOGGER.warn("username: " + username + " tried to login more than 5 times in period");
			throw new ExcessiveAttemptsException();
		} else {
			passwordRetryCache.put(username, retryCount);
		}
		String inPassword = getInPassword(utoken);
		String dbPassword = (String) info.getCredentials();
		boolean matches = this.equals(inPassword, dbPassword);
		if (matches) {
			passwordRetryCache.remove(username);
		}
		return matches;
	}

	/**
	 * 对token中获取的密码进行特殊处理
	 * 
	 * @param utoken
	 * @return
	 */
	private String getInPassword(UsernamePasswordToken utoken) {
		return String.valueOf(utoken.getPassword());
	}
}
