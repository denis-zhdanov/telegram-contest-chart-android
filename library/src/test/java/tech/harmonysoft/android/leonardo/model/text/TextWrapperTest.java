package tech.harmonysoft.android.leonardo.model.text;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Denis Zhdanov
 * @since 22/3/19
 */
class TextWrapperTest {

    private TextWrapper mText;

    @BeforeEach
    public void setUp() {
        mText = new TextWrapper();
    }

    @Test
    public void whenZeroIsPut_thenItIsCorrectlyConvertedToString() {
        mText.append(0);
        assertThat(mText.toString()).isEqualTo("0");
    }

    @Test
    public void whenMinusOneIsPut_thenItIsCorrectlyConvertedToString() {
        mText.append(-1);
        assertThat(mText.toString()).isEqualTo("-1");
    }

    @Test
    public void whenOneIsPut_thenItIsCorrectlyConvertedToString() {
        mText.append(1);
        assertThat(mText.toString()).isEqualTo("1");
    }

    @Test
    public void whenNineIsPut_thenItIsCorrectlyConvertedToString() {
        mText.append(9);
        assertThat(mText.toString()).isEqualTo("9");
    }

    @Test
    public void whenMinusNineIsPut_thenItIsCorrectlyConvertedToString() {
        mText.append(-9);
        assertThat(mText.toString()).isEqualTo("-9");
    }

    @Test
    public void whenNinetyNineIsPut_thenItIsCorrectlyConvertedToString() {
        mText.append(99);
        assertThat(mText.toString()).isEqualTo("99");
    }

    @Test
    public void whenMinusNinetyNineIsPut_thenItIsCorrectlyConvertedToString() {
        mText.append(-99);
        assertThat(mText.toString()).isEqualTo("-99");
    }

    @Test
    public void whenHundredIsPut_thenItIsCorrectlyConvertedToString() {
        mText.append(100);
        assertThat(mText.toString()).isEqualTo("100");
    }

    @Test
    public void whenMinusHundredIsPut_thenItIsCorrectlyConvertedToString() {
        mText.append(-100);
        assertThat(mText.toString()).isEqualTo("-100");
    }
}