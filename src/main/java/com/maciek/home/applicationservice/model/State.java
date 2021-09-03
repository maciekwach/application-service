package com.maciek.home.applicationservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum State {
    CREATED, ACCEPTED, VERIFIED, REJECTED, PUBLISHED
}
