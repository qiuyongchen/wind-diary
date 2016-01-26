package com.qiuyongchen.diary.util;

import com.qiuyongchen.diary.data.DiaryItem;

import java.util.Comparator;

/**
 * 对从数据库内取出的数据进行排序，避免各年的同一天都排在一起
 * Created by qiuyongchen on 2016/1/23.
 */
public class ComparatorDiaryItem implements Comparator<DiaryItem> {
    /**
     * Compares the two specified objects to determine their relative ordering. The ordering
     * implied by the return value of this method for all possible pairs of
     * {@code (lhs, rhs)} should form an <i>equivalence relation</i>.
     * This means that
     * <ul>
     * <li>{@code compare(a,a)} returns zero for all {@code a}</li>
     * <li>the sign of {@code compare(a,b)} must be the opposite of the sign of {@code
     * compare(b,a)} for all pairs of (a,b)</li>
     * <li>From {@code compare(a,b) > 0} and {@code compare(b,c) > 0} it must
     * follow {@code compare(a,c) > 0} for all possible combinations of {@code
     * (a,b,c)}</li>
     * </ul>
     *
     * @param lhs an {@code Object}.
     * @param rhs a second {@code Object} to compare with {@code lhs}.
     * @return an integer < 0 if {@code lhs} is less than {@code rhs}, 0 if they are
     * equal, and > 0 if {@code lhs} is greater than {@code rhs}.
     * @throws ClassCastException if objects are not of the correct type.
     */
    @Override
    public int compare(DiaryItem lhs, DiaryItem rhs) {
        if (Integer.valueOf(lhs.getDate()) < Integer.valueOf(rhs.getDate())) {
            return -1;
        }

        if (Integer.valueOf(lhs.getDate()) > Integer.valueOf(rhs.getDate())) {
            return 1;
        }

        if (Integer.valueOf(lhs.getTime().substring(0, 2) + lhs.getTime().substring(3, 5)
                + lhs.getTime().substring(6, 8)) <
                Integer.valueOf(rhs.getTime().substring(0, 2) + rhs.getTime().substring(3, 5)
                        + rhs.getTime().substring(6, 8))) {
            return -1;
        }

        if (Integer.valueOf(lhs.getTime().substring(0, 2) + lhs.getTime().substring(3, 5)
                + lhs.getTime().substring(6, 8)) >
                Integer.valueOf(rhs.getTime().substring(0, 2) + rhs.getTime().substring(3, 5)
                        + rhs.getTime().substring(6, 8))) {
            return 1;
        }

        return 0;
    }
}
