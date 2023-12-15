package io.nuvalence.workmanager.service.domain.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class BusinessTypeTest {
    @Test
    public void testToString() {
        assertEquals("CORPORATION", BusinessType.CORPORATION.toString());
        assertEquals("SOLE_PROPRIETORSHIP", BusinessType.SOLE_PROPRIETORSHIP.toString());
    }

    @Test
    public void testFromValueValid() {
        assertSame(BusinessType.CORPORATION, BusinessType.fromValue("CORPORATION"));
        assertSame(BusinessType.SOLE_PROPRIETORSHIP, BusinessType.fromValue("SOLE_PROPRIETORSHIP"));
    }

    @Test
    public void testFromValueInvalid() {
        assertThrows(IllegalArgumentException.class, () -> BusinessType.fromValue("INVALID_VALUE"));
    }
}
