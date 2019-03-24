package tech.harmonysoft.android.leonardo.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.harmonysoft.android.leonardo.model.Range;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Denis Zhdanov
 * @since 13/3/19
 */
class RangesListTest {

    private RangesList mCollection;

    @BeforeEach
    public void setUp() {
        mCollection = new RangesList();
    }

    @Test
    public void whenSingleRangeIsGiven_thenItIsKepAsIs() {
        mCollection.add(new Range(2, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(2, 3));
    }

    @Test
    public void whenNewRangeIsAdjacentToExistingFromRight_thenTheyAreMerged() {
        mCollection.add(new Range(0, 1));
        mCollection.add(new Range(1, 2));
        assertThat(mCollection.getRanges()).containsOnly(new Range(0, 2));
    }

    @Test
    public void whenNewRangeIsAdjacentToExistingFromLeft_thenTheyAreMerged() {
        mCollection.add(new Range(1, 2));
        mCollection.add(new Range(0, 1));
        assertThat(mCollection.getRanges()).containsOnly(new Range(0, 2));
    }

    @Test
    public void whenNewRangeStartsBeforeExisting_andEndsBeforeExisting_thenTheyAreMerged() {
        mCollection.add(new Range(1, 3));
        mCollection.add(new Range(0, 2));
        assertThat(mCollection.getRanges()).containsOnly(new Range(0, 3));
    }

    @Test
    public void whenNewRangeStartsBeforeExisting_andEndsWithExisting_thenTheyAreMerged() {
        mCollection.add(new Range(1, 2));
        mCollection.add(new Range(0, 2));
        assertThat(mCollection.getRanges()).containsOnly(new Range(0, 2));
    }

    @Test
    public void whenNewRangeStartsBeforeExisting_andEndsAfterExisting_thenTheyAreMerged() {
        mCollection.add(new Range(1, 2));
        mCollection.add(new Range(0, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(0, 3));
    }

    @Test
    public void whenNewRangeStartsWithExisting_andEndsBeforeExisting_thenTheyAreMerged() {
        mCollection.add(new Range(0, 3));
        mCollection.add(new Range(0, 2));
        assertThat(mCollection.getRanges()).containsOnly(new Range(0, 3));
    }

    @Test
    public void whenNewRangeStartsWithExisting_andEndsWithExisting_thenTheyAreMerged() {
        mCollection.add(new Range(0, 2));
        mCollection.add(new Range(0, 2));
        assertThat(mCollection.getRanges()).containsOnly(new Range(0, 2));
    }

    @Test
    public void whenNewRangeStartsWithExisting_andEndsAfterExisting_thenTheyAreMerged() {
        mCollection.add(new Range(0, 2));
        mCollection.add(new Range(0, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(0, 3));
    }

    @Test
    public void whenNewRangeStartsAfterExisting_andEndsBeforeExisting_thenTheyAreMerged() {
        mCollection.add(new Range(0, 3));
        mCollection.add(new Range(1, 2));
        assertThat(mCollection.getRanges()).containsOnly(new Range(0, 3));
    }

    @Test
    public void whenNewRangeStartsAfterExisting_andEndsWithExisting_thenTheyAreMerged() {
        mCollection.add(new Range(0, 3));
        mCollection.add(new Range(1, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(0, 3));
    }

    @Test
    public void whenNewRangeStartsAfterExisting_andEndsAfterExisting_thenTheyAreMerged() {
        mCollection.add(new Range(0, 2));
        mCollection.add(new Range(1, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(0, 3));
    }

    @Test
    public void whenNewRangeIntersectsWithTwoExisting_thenTheyAreMerged() {
        mCollection.add(new Range(0, 2));
        mCollection.add(new Range(4, 6));
        mCollection.add(new Range(1, 5));
        assertThat(mCollection.getRanges()).containsOnly(new Range(0, 6));
    }

    @Test
    public void whenNewRangeCoversTwoExisting_thenTheyAreMerged() {
        mCollection.add(new Range(2, 3));
        mCollection.add(new Range(5, 6));
        mCollection.add(new Range(1, 7));
        assertThat(mCollection.getRanges()).containsOnly(new Range(1, 7));
    }

    @Test
    public void given1range_whenItStartsBeforeStartAndEndsBeforeStart_thenContainsReturnsFalse() {
        mCollection.add(new Range(1, 2));
        assertThat(mCollection.contains(new Range(3, 4))).isFalse();
    }

    @Test
    public void given1range_whenItStartsBeforeStartAndEndsWithStart_thenContainsReturnsFalse() {
        mCollection.add(new Range(1, 2));
        assertThat(mCollection.contains(new Range(2, 4))).isFalse();
    }

    @Test
    public void given1range_whenItStartsBeforeStartAndEndsBeforeEnd_thenContainsReturnsFalse() {
        mCollection.add(new Range(1, 4));
        assertThat(mCollection.contains(new Range(3, 5))).isFalse();
    }

    @Test
    public void given1range_whenThereIsStartsBeforeStartAndEndsWith_thenContainsReturnsTrue() {
        mCollection.add(new Range(1, 3));
        assertThat(mCollection.contains(new Range(2, 3))).isTrue();
    }

    @Test
    public void given1range_whenThereIsStartsBeforeStartAndEndsAfterEnd_thenContainsReturnsTrue() {
        mCollection.add(new Range(1, 4));
        assertThat(mCollection.contains(new Range(2, 3))).isTrue();
    }

    @Test
    public void given1range_whenItStartsWithStartAndEndsBeforeEnd_thenContainsReturnsFalse() {
        mCollection.add(new Range(1, 2));
        assertThat(mCollection.contains(new Range(1, 3))).isFalse();
    }

    @Test
    public void given1range_whenItStartsWithStartAndEndsWithEnd_thenContainsReturnsTrue() {
        mCollection.add(new Range(1, 2));
        assertThat(mCollection.contains(new Range(1, 2))).isTrue();
    }

    @Test
    public void given1range_whenItStartsWithStartAndEndsAfterEnd_thenContainsReturnsTrue() {
        mCollection.add(new Range(1, 3));
        assertThat(mCollection.contains(new Range(1, 2))).isTrue();
    }

    @Test
    public void given1range_whenItStartsAfterStartAndEndsBeforeEnd_thenContainsReturnsFalse() {
        mCollection.add(new Range(2, 3));
        assertThat(mCollection.contains(new Range(1, 4))).isFalse();
    }

    @Test
    public void given1range_whenItStartsAfterStartAndEndsWithEnd_thenContainsReturnsFalse() {
        mCollection.add(new Range(2, 3));
        assertThat(mCollection.contains(new Range(1, 3))).isFalse();
    }

    @Test
    public void given1range_whenItStartsAfterStartAndEndsAfterEnd_thenContainsReturnsFalse() {
        mCollection.add(new Range(2, 4));
        assertThat(mCollection.contains(new Range(1, 3))).isFalse();
    }

    @Test
    public void given1range_whenItStartsWithEnd_thenContainsReturnsFalse() {
        mCollection.add(new Range(2, 4));
        assertThat(mCollection.contains(new Range(1, 2))).isFalse();
    }

    @Test
    public void given1range_whenItStartsAfterEnd_thenContainsReturnsFalse() {
        mCollection.add(new Range(3, 4));
        assertThat(mCollection.contains(new Range(1, 2))).isFalse();
    }

    @Test
    public void given2ranges_thenContainsReturnsFalse() {
        mCollection.add(new Range(1, 3));
        mCollection.add(new Range(5, 7));
        assertThat(mCollection.contains(new Range(2, 6))).isFalse();
    }

    @Test
    public void given1range_whenItStartsBeforeStartAndEndsBeforeStart_thenItIsNotKept() {
        mCollection.add(new Range(1, 2));
        mCollection.keepOnly(new Range(3, 4));
        assertThat(mCollection.getRanges()).isEmpty();
    }

    @Test
    public void given1range_whenItStartsBeforeStartAndEndsWithStart_thenPointIsKept() {
        mCollection.add(new Range(1, 2));
        mCollection.keepOnly(new Range(2, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(2, 2));
    }

    @Test
    public void given1range_whenItStartsBeforeStartAndEndsBeforeEnd_thenKeepCutsIt() {
        mCollection.add(new Range(1, 3));
        mCollection.keepOnly(new Range(2, 4));
        assertThat(mCollection.getRanges()).containsOnly(new Range(2, 3));
    }

    @Test
    public void given1range_whenItStartsBeforeStartAndEndsWithEnd_thenKeepCutsIt() {
        mCollection.add(new Range(1, 3));
        mCollection.keepOnly(new Range(2, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(2, 3));
    }

    @Test
    public void given1range_whenItStartsBeforeStartAndEndsAfterEnd_thenKeepCutsIt() {
        mCollection.add(new Range(1, 4));
        mCollection.keepOnly(new Range(2, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(2, 3));
    }

    @Test
    public void given1range_whenItStartsWithStartAndEndsBeforeEnd_thenItIsKept() {
        mCollection.add(new Range(1, 3));
        mCollection.keepOnly(new Range(1, 4));
        assertThat(mCollection.getRanges()).containsOnly(new Range(1, 3));
    }

    @Test
    public void given1range_whenItStartsWithStartAndEndsWithEnd_thenItIsKept() {
        mCollection.add(new Range(1, 3));
        mCollection.keepOnly(new Range(1, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(1, 3));
    }

    @Test
    public void given1range_whenItStartsWithStartAndEndsAfterEnd_thenItIsCut() {
        mCollection.add(new Range(1, 4));
        mCollection.keepOnly(new Range(1, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(1, 3));
    }

    @Test
    public void given1range_whenItStartsAfterStartAndEndsBeforeEnd_thenItIsKept() {
        mCollection.add(new Range(2, 4));
        mCollection.keepOnly(new Range(1, 5));
        assertThat(mCollection.getRanges()).containsOnly(new Range(2, 4));
    }

    @Test
    public void given1range_whenItStartsAfterStartAndEndsWithEnd_thenItIsKept() {
        mCollection.add(new Range(2, 4));
        mCollection.keepOnly(new Range(1, 4));
        assertThat(mCollection.getRanges()).containsOnly(new Range(2, 4));
    }

    @Test
    public void given1range_whenItStartsAfterStartAndEndsAfterEnd_thenItIsCut() {
        mCollection.add(new Range(2, 4));
        mCollection.keepOnly(new Range(1, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(2, 3));
    }

    @Test
    public void given1range_whenItStartsWithEndAndEndsAfterEnd_thenItIsCut() {
        mCollection.add(new Range(3, 5));
        mCollection.keepOnly(new Range(1, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(3, 3));
    }

    @Test
    public void given2ranges_whenTheyIntersect_thenTheyAreCur() {
        mCollection.add(new Range(1, 3));
        mCollection.add(new Range(6, 9));
        mCollection.keepOnly(new Range(2, 7));
        assertThat(mCollection.getRanges()).containsOnly(new Range(2, 3), new Range(6, 7));
    }

    @Test
    public void whenThereIsPoint_theIsMergedFromLeft() {
        mCollection.add(new Range(1, 2));
        mCollection.keepOnly(new Range(2, 3));
        mCollection.add(new Range(3, 4));
        assertThat(mCollection.getRanges()).containsOnly(new Range(2, 4));
    }

    @Test
    public void whenThereIsPoint_theIsMergedFromStart() {
        mCollection.add(new Range(1, 2));
        mCollection.keepOnly(new Range(2, 3));
        mCollection.add(new Range(2, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(2, 3));
    }

    @Test
    public void whenThereIsPoint_theIsMergedInBetween() {
        mCollection.add(new Range(1, 2));
        mCollection.keepOnly(new Range(2, 3));
        mCollection.add(new Range(1, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(1, 3));
    }

    @Test
    public void whenThereIsPoint_theIsMergedFromEnd() {
        mCollection.add(new Range(3, 4));
        mCollection.keepOnly(new Range(4, 5));
        mCollection.add(new Range(3, 4));
        assertThat(mCollection.getRanges()).containsOnly(new Range(3, 4));
    }

    @Test
    public void whenThereIsPoint_theIsMergedFromRight() {
        mCollection.add(new Range(3, 4));
        mCollection.keepOnly(new Range(4, 5));
        mCollection.add(new Range(2, 3));
        assertThat(mCollection.getRanges()).containsOnly(new Range(2, 4));
    }

    @Test
    public void given1range_whenItStartsBeforeStartAndEndsBeforeStart_thenMissingIsCorrect() {
        mCollection.add(new Range(1, 2));
        assertThat(mCollection.getMissing(new Range(3, 4))).containsOnly(new Range(3, 4));
    }

    @Test
    public void given1range_whenItStartsBeforeStartAndEndsWithStart_thenMissingIsCorrect() {
        mCollection.add(new Range(1, 2));
        assertThat(mCollection.getMissing(new Range(2, 3))).containsOnly(new Range(3, 3));
    }

    @Test
    public void given1range_whenItStartsBeforeStartAndEndsAfterStart_thenMissingIsCorrect() {
        mCollection.add(new Range(1, 3));
        assertThat(mCollection.getMissing(new Range(2, 5))).containsOnly(new Range(4, 5));
    }

    @Test
    public void given1range_whenItStartsBeforeStartAndEndsWithEnd_thenMissingIsCorrect() {
        mCollection.add(new Range(1, 3));
        assertThat(mCollection.getMissing(new Range(2, 3))).isEmpty();
    }

    @Test
    public void given1range_whenItStartsBeforeStartAndEndsAfterEnd_thenMissingIsCorrect() {
        mCollection.add(new Range(1, 4));
        assertThat(mCollection.getMissing(new Range(2, 3))).isEmpty();
    }

    @Test
    public void given1range_whenItStartsWithStartAndEndsWithStart_thenMissingIsCorrect() {
        mCollection.add(new Range(1, 1));
        assertThat(mCollection.getMissing(new Range(1, 3))).containsOnly(new Range(2, 3));
    }

    @Test
    public void given1range_whenItStartsWithStartAndEndsBeforeEnd_thenMissingIsCorrect() {
        mCollection.add(new Range(1, 2));
        assertThat(mCollection.getMissing(new Range(1, 3))).containsOnly(new Range(3, 3));
    }

    @Test
    public void given1range_whenItStartsWithStartAndEndsWithEnd_thenMissingIsCorrect() {
        mCollection.add(new Range(1, 2));
        assertThat(mCollection.getMissing(new Range(1, 2))).isEmpty();
    }

    @Test
    public void given1range_whenItStartsWithStartAndEndsAfterEnd_thenMissingIsCorrect() {
        mCollection.add(new Range(1, 3));
        assertThat(mCollection.getMissing(new Range(1, 2))).isEmpty();
    }

    @Test
    public void given1range_whenItStartsAfterStartAndEndsAfterStart_thenMissingIsCorrect() {
        mCollection.add(new Range(2, 3));
        assertThat(mCollection.getMissing(new Range(1, 5))).containsOnly(new Range(1, 1), new Range(4, 5));
    }

    @Test
    public void given1range_whenItStartsAfterStartAndEndsWithEnd_thenMissingIsCorrect() {
        mCollection.add(new Range(2, 3));
        assertThat(mCollection.getMissing(new Range(1, 3))).containsOnly(new Range(1, 1));
    }

    @Test
    public void given1range_whenItStartsAfterStartAndEndsAfterEnd_thenMissingIsCorrect() {
        mCollection.add(new Range(2, 4));
        assertThat(mCollection.getMissing(new Range(1, 3))).containsOnly(new Range(1, 1));
    }

    @Test
    public void given1range_whenItStartsWithEndAndEndsWithEnd_thenMissingIsCorrect() {
        mCollection.add(new Range(2, 2));
        assertThat(mCollection.getMissing(new Range(1, 2))).containsOnly(new Range(1, 1));
    }

    @Test
    public void given1range_whenItStartsWithEndAndEndsAfterEnd_thenMissingIsCorrect() {
        mCollection.add(new Range(2, 4));
        assertThat(mCollection.getMissing(new Range(1, 3))).containsOnly(new Range(1, 1));
    }

    @Test
    public void given2range_whenTheyIntersectWithGiven_thenMissingIsCorrect() {
        mCollection.add(new Range(1, 3));
        mCollection.add(new Range(6, 8));
        assertThat(mCollection.getMissing(new Range(2, 7))).containsOnly(new Range(4, 5));
    }

    @Test
    public void givenNoRanges_thenMissingIsCorrect() {
        assertThat(mCollection.getMissing(new Range(2, 8))).containsOnly(new Range(2, 8));
    }

    @Test
    public void givenNoCurrentRange_whenRangeWithNegativeStartIsGiven_thenMissingIsCorrect() {
        assertThat(mCollection.getMissing(new Range(-401, 543))).containsOnly(new Range(-401, 543));
    }
}