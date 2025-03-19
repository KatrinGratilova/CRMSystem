package org.example.crmsystem.service;

import java.util.Date;

public record TokenWithMetadata(String token, Date issuedAt, Date expiration) {
}
