package net.socialgamer.cah.servlets;

import net.socialgamer.cah.data.User;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class Providers {
    private final static Logger logger = Logger.getLogger(Providers.class.getSimpleName());
    private final static Map<Class<? extends Annotation>, Provider<?>> providers = new HashMap<Class<? extends Annotation>, Provider<?>>() {
        @Override
        public Provider<?> put(Class<? extends Annotation> key, Provider<?> value) {
            logger.config("Added provider for " + key);
            return super.put(key, value);
        }
    };

    static {
        // providers.put(Annotations.HibernateSession.class, (Provider<Session>) HibernateUtil.instance.sessionFactory::openSession); FIXME

        add(Annotations.UserFactory.class, new Provider<User.Factory>() {
            @Override
            public User.Factory get() {
                return new User.Factory() {
                    @Override
                    public User create(String nickname, String hostname, boolean isAdmin, String persistentId, @Nullable String clientLanguage, @Nullable String clientAgent) {
                        return new User(nickname, hostname, isAdmin, persistentId, Sessions.generateNewId(), clientLanguage == null ? "" : clientLanguage, clientAgent == null ? "" : clientAgent);
                    }
                };
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <P> Provider<P> get(Class<? extends Annotation> cls) {
        return (Provider<P>) providers.get(cls);
    }

    public static void add(Class<? extends Annotation> cls, Provider<?> provider) {
        providers.put(cls, provider);
    }
}
