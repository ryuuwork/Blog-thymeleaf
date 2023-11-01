//package com.tuananhdo.security.oauth;
//
//import org.springframework.context.annotation.Lazy;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Component;
//
//import java.util.Collection;
//import java.util.Map;
//
//@Component
//@Lazy
//public class OAuth2BlogUser implements OAuth2User {
//    private final OAuth2User oAuth2User;
//
//    public OAuth2BlogUser(OAuth2User oAuth2User) {
//        this.oAuth2User = oAuth2User;
//    }
//
//    @Override
//    public Map<String, Object> getAttributes() {
//        return oAuth2User.getAttributes();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return oAuth2User.getAuthorities();
//    }
//
//    @Override
//    public String getName() {
//        return oAuth2User.getAttribute("name");
//    }
//
//    public String getFullName() {
//        return oAuth2User.getAttribute("name");
//    }
//
//    public String getEmail(){
//        return oAuth2User.getAttribute("email");
//    }
//
//}
