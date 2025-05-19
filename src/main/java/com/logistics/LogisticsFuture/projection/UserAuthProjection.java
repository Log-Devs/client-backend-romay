package com.logistics.LogisticsFuture.projection;

import java.util.UUID;

public interface UserAuthProjection {
    UUID getUserId();
    String getEmail();
    String getPassword();
}
