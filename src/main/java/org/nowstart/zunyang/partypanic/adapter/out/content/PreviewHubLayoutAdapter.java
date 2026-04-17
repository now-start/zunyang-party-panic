package org.nowstart.zunyang.partypanic.adapter.out.content;

import java.util.List;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubLayoutPort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.hub.HubHotspot;
import org.nowstart.zunyang.partypanic.domain.hub.HubLayout;

public final class PreviewHubLayoutAdapter implements LoadHubLayoutPort {

    @Override
    public HubLayout load() {
        return new HubLayout(
            7,
            5,
            new Position(3, 2),
            List.of(
                hotspot(
                    ChapterId.SIGNAL,
                    "첫 신호",
                    1,
                    4,
                    "첫 신호 큐를 맞추는 자리다. 여기부터 밤의 리듬이 시작된다.",
                    "가장 먼저 점검할 신호선부터 맞춘다."
                ),
                hotspot(
                    ChapterId.PROPS,
                    "소품 회수",
                    3,
                    4,
                    "빈자리를 채울 물건을 고르면 허브 전체의 톤이 살아난다.",
                    "아직 첫 신호가 정리되지 않았다. 먼저 큐를 고정한다."
                ),
                hotspot(
                    ChapterId.CENTERPIECE,
                    "중심 연출",
                    5,
                    4,
                    "가운데 장면이 완성되면 준비동의 인상이 단번에 달라진다.",
                    "소품 회수가 끝나야 중심 연출을 올릴 수 있다."
                ),
                hotspot(
                    ChapterId.PHOTO,
                    "포토 베이",
                    1,
                    0,
                    "남을 장면은 대충 잡으면 오래 아쉽다. 프레임부터 다시 본다.",
                    "중심 연출의 톤이 먼저 잡혀야 포토 베이를 연출할 수 있다."
                ),
                hotspot(
                    ChapterId.HANDOVER,
                    "기록 복도",
                    3,
                    0,
                    "이 밤이 처음이 아니라는 걸 알려 주는 인수인계 구간이다.",
                    "포토 베이 기준이 정리되기 전에는 기록 복도를 열지 않는다."
                ),
                hotspot(
                    ChapterId.MESSAGE,
                    "메시지 월",
                    5,
                    0,
                    "무대 곁에 어떤 말을 남길지 정하는 마지막 정리 파트다.",
                    "기록 복도의 맥락이 정리돼야 메시지 월 문장이 선다."
                ),
                hotspot(
                    ChapterId.FINALE,
                    "피날레",
                    6,
                    2,
                    "불이 켜지기 직전, 조력자는 한 발 물러난 자리에서 최종 신호를 보낸다.",
                    "마지막 메시지가 정리되기 전에는 피날레 신호를 올리지 않는다."
                )
            )
        );
    }

    private static HubHotspot hotspot(
        ChapterId chapterId,
        String label,
        int x,
        int y,
        String interactionText,
        String lockedText
    ) {
        return new HubHotspot(chapterId, label, new Position(x, y), interactionText, lockedText);
    }
}
