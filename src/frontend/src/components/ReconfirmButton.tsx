import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import {
    AlertDialog,
    AlertDialogTrigger,
    AlertDialogContent,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogCancel,
    AlertDialogAction,
} from "@/components/ui/alert-dialog";

type ReservationStatus = "CANCELLED" | "CONFIRMED";

interface ReconfirmButtonProps {
    reservationId: number;
    initialStatus: ReservationStatus;
    ttlSeconds: number;
    onReconfirm: (id: number) => Promise<void>;
}

export default function ReconfirmButton({
                                            reservationId,
                                            initialStatus,
                                            ttlSeconds,
                                            onReconfirm,
                                        }: ReconfirmButtonProps) {
    const [status, setStatus] = useState<ReservationStatus>(initialStatus);
    const [timeLeft, setTimeLeft] = useState(ttlSeconds);
    const [error, setError] = useState<string | null>(null);

    // TTL 카운트다운
    useEffect(() => {
        if (status !== "CANCELLED" || timeLeft <= 0) return;

        const timer = setInterval(() => {
            setTimeLeft((prev) => (prev > 0 ? prev - 1 : 0));
        }, 1000);

        return () => clearInterval(timer);
    }, [status]); // ✅ timeLeft 제거 → 불필요한 interval 재생성 방지

    const formatTime = (seconds: number) => {
        const m = Math.floor(seconds / 60);
        const s = seconds % 60;
        return `${m}:${s.toString().padStart(2, "0")}`;
    };

    const handleClick = async () => {
        try {
            await onReconfirm(reservationId);
            setStatus("CONFIRMED");
            setError(null);
        } catch (err) {
            console.error("재확정 실패", err);
            setError("재확정에 실패했습니다. 다시 시도해주세요.");
        }
    };

    // ✅ 예매 확정됨
    if (status === "CONFIRMED") {
        return <span className="text-green-600 font-medium">예매완료</span>;
    }

    // ✅ TTL 만료
    if (timeLeft <= 0) {
        return <span className="text-gray-400">재확정 불가 (만료)</span>;
    }

    return (
        <div className="flex items-center gap-2">
            <AlertDialog>
                <AlertDialogTrigger asChild>
                    <Button variant="secondary" className="font-semibold">
                        🔄 재확정
                    </Button>
                </AlertDialogTrigger>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>예매 재확정</AlertDialogTitle>
                        <AlertDialogDescription>
                            이 예매는 현재 <span className="font-bold text-red-500">취소 상태</span>입니다.
                            <br />
                            재확정은 <span className="text-red-500">단 한 번만</span> 가능하며,
                            남은 시간 {formatTime(timeLeft)} 내에만 실행할 수 있습니다.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel>취소</AlertDialogCancel>
                        <AlertDialogAction onClick={handleClick}>확인</AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>

            <span className="text-sm text-gray-500">
        남은 시간 {formatTime(timeLeft)}
      </span>

            {error && <span className="text-sm text-red-500">{error}</span>}
        </div>
    );
}
