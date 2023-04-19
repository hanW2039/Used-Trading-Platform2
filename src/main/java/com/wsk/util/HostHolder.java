package com.wsk.util;

import com.wsk.pojo.UserInformation;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，代替session对象
 * @author hanW
 * @create 2022-08-04 12:59
 */
@Component
public class HostHolder {
    private ThreadLocal<UserInformation> users = new ThreadLocal<>();
    public void setUser(UserInformation user){
        users.set(user);
    }
    public UserInformation getUser(){
        return users.get();
    }
    public void clear(){
        users.remove();
    }

    public void get() {
    }
}
