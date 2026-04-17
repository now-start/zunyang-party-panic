package org.nowstart.zunyang.partypanic.support;

import java.util.Map;
import org.nowstart.zunyang.partypanic.domain.centerpiece.CenterpiecePlacementId;
import org.nowstart.zunyang.partypanic.domain.common.GridActivityLayout;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.finale.FinaleCheckpointId;
import org.nowstart.zunyang.partypanic.domain.handover.HandoverClueId;
import org.nowstart.zunyang.partypanic.domain.message.MessageNoteId;
import org.nowstart.zunyang.partypanic.domain.photo.PhotoFocusId;
import org.nowstart.zunyang.partypanic.domain.props.PropsItemId;
import org.nowstart.zunyang.partypanic.domain.signal.SignalControlId;

public final class ActivityTestLayouts {

    private ActivityTestLayouts() {
    }

    public static GridActivityLayout<SignalControlId> signalConsole() {
        return new GridActivityLayout<>(
            5,
            5,
            new Position(2, 2),
            Map.of(
                SignalControlId.MIC, new Position(1, 3),
                SignalControlId.LAMP, new Position(3, 3),
                SignalControlId.MONITOR, new Position(1, 1),
                SignalControlId.CUE, new Position(3, 1)
            )
        );
    }

    public static GridActivityLayout<PropsItemId> propsArchive() {
        return new GridActivityLayout<>(
            7,
            5,
            new Position(3, 2),
            Map.of(
                PropsItemId.RIBBON_BOX, new Position(1, 3),
                PropsItemId.TOPPER_CASE, new Position(3, 3),
                PropsItemId.GEL_PACK, new Position(5, 3),
                PropsItemId.FABRIC_ROLL, new Position(1, 1),
                PropsItemId.BASKET_SET, new Position(3, 1),
                PropsItemId.MEMORY_DECOR, new Position(5, 1)
            )
        );
    }

    public static GridActivityLayout<CenterpiecePlacementId> centerpieceTable() {
        return new GridActivityLayout<>(
            7,
            5,
            new Position(3, 2),
            Map.of(
                CenterpiecePlacementId.TOPPER_SLOT, new Position(3, 4),
                CenterpiecePlacementId.CANDLE_ARC, new Position(1, 2),
                CenterpiecePlacementId.RIBBON_LINE, new Position(5, 2),
                CenterpiecePlacementId.CLOTH_FOLD, new Position(3, 0),
                CenterpiecePlacementId.SIDE_DECOR, new Position(5, 4)
            )
        );
    }

    public static GridActivityLayout<PhotoFocusId> photoBay() {
        return new GridActivityLayout<>(
            7,
            5,
            new Position(3, 2),
            Map.of(
                PhotoFocusId.FRAME_GUIDE, new Position(3, 4),
                PhotoFocusId.STOOL_MARK, new Position(1, 2),
                PhotoFocusId.KEY_LIGHT, new Position(5, 2),
                PhotoFocusId.BACKDROP_LINE, new Position(3, 0),
                PhotoFocusId.FLOOR_DECOR, new Position(5, 4)
            )
        );
    }

    public static GridActivityLayout<HandoverClueId> handoverCorridor() {
        return new GridActivityLayout<>(
            7,
            5,
            new Position(3, 2),
            Map.of(
                HandoverClueId.OLD_CUESHEET, new Position(1, 3),
                HandoverClueId.PHOTO_FRAME, new Position(3, 3),
                HandoverClueId.MEMO_BOARD, new Position(5, 3),
                HandoverClueId.PROJECTOR, new Position(1, 1),
                HandoverClueId.ACCESS_PASS, new Position(3, 1),
                HandoverClueId.PACKED_BOX, new Position(5, 1)
            )
        );
    }

    public static GridActivityLayout<MessageNoteId> messageWall() {
        return new GridActivityLayout<>(
            7,
            5,
            new Position(3, 2),
            Map.of(
                MessageNoteId.FIRST_GREETING, new Position(1, 3),
                MessageNoteId.QUIET_MOMENT, new Position(5, 3),
                MessageNoteId.WAITING_LINE, new Position(1, 1),
                MessageNoteId.MEMORY_WISH, new Position(3, 1),
                MessageNoteId.TONIGHT_WISH, new Position(5, 1)
            )
        );
    }

    public static GridActivityLayout<FinaleCheckpointId> finaleStage() {
        return new GridActivityLayout<>(
            7,
            5,
            new Position(3, 2),
            Map.of(
                FinaleCheckpointId.COUNTDOWN_LAMP, new Position(1, 3),
                FinaleCheckpointId.STREAMER_MARK, new Position(3, 3),
                FinaleCheckpointId.GO_CUE_PANEL, new Position(5, 3),
                FinaleCheckpointId.SIDE_CURTAIN, new Position(1, 1),
                FinaleCheckpointId.PROP_TABLE, new Position(5, 1)
            )
        );
    }
}
