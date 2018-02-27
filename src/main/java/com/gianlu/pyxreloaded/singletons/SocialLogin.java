package com.gianlu.pyxreloaded.singletons;

import com.gianlu.pyxreloaded.Consts;
import com.gianlu.pyxreloaded.facebook.FacebookAuthHelper;
import com.gianlu.pyxreloaded.facebook.FacebookOAuthException;
import com.gianlu.pyxreloaded.facebook.FacebookProfileInfo;
import com.gianlu.pyxreloaded.facebook.FacebookToken;
import com.gianlu.pyxreloaded.github.GithubAuthHelper;
import com.gianlu.pyxreloaded.github.GithubProfileInfo;
import com.gianlu.pyxreloaded.server.BaseCahHandler;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public final class SocialLogin {
    private final GithubAuthHelper githubHelper;
    private final GoogleIdTokenVerifier googleHelper;
    private final FacebookAuthHelper facebookHelper;

    public SocialLogin(GithubAuthHelper githubHelper, Preferences preferences) {
        this.githubHelper = githubHelper;
        googleHelper = new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(preferences.getString("googleClientId", "")))
                .build();

        facebookHelper = new FacebookAuthHelper(preferences.getString("facebookAppId", ""), preferences.getString("facebookAppSecret", ""));
    }

    @Nullable
    public GoogleIdToken.Payload verifyGoogle(String tokenStr) throws BaseCahHandler.CahException {
        if (tokenStr == null) return null;

        try {
            GoogleIdToken token = googleHelper.verify(tokenStr);
            return token == null ? null : token.getPayload();
        } catch (GeneralSecurityException | IOException ex) {
            throw new BaseCahHandler.CahException(Consts.ErrorCode.GOOGLE_ERROR, ex);
        }
    }

    @NotNull
    public FacebookProfileInfo infoFacebook(String userId) throws BaseCahHandler.CahException {
        try {
            return facebookHelper.info(userId);
        } catch (FacebookOAuthException ex) {
            throw new BaseCahHandler.CahException(Consts.ErrorCode.FACEBOOK_INVALID_TOKEN, ex);
        } catch (IOException ex) {
            throw new BaseCahHandler.CahException(Consts.ErrorCode.FACEBOOK_ERROR, ex);
        }
    }

    @Nullable
    public FacebookToken verifyFacebook(String accessToken) throws BaseCahHandler.CahException {
        if (accessToken == null) return null;

        try {
            return facebookHelper.verify(accessToken);
        } catch (IOException ex) {
            throw new BaseCahHandler.CahException(Consts.ErrorCode.FACEBOOK_ERROR, ex);
        } catch (FacebookOAuthException ex) {
            throw new BaseCahHandler.CahException(Consts.ErrorCode.FACEBOOK_INVALID_TOKEN, ex);
        }
    }

    @NotNull
    public GithubProfileInfo infoGithub(String accessToken) throws BaseCahHandler.CahException {
        try {
            return githubHelper.info(accessToken);
        } catch (IOException ex) {
            throw new BaseCahHandler.CahException(Consts.ErrorCode.GITHUB_ERROR, ex);
        }
    }
}
