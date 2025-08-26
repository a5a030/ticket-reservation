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
    ttlSeconds: number; // 백엔드에서 TTL 내려주면 표시
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

    // TTL 카운트다운
    useEffect(() => {
        if (status !== "CANCELLED" || timeLeft <= 0) return;

        const timer = setInterval(() => {
            setTimeLeft((prev) => (prev > 0 ? prev - 1 : 0));
        }, 1000);

        return () => clearInterval(timer);
    }, [status, timeLeft]);

    const formatTime = (seconds: number) => {
        const m = Math.floor(seconds / 60);
        const s = seconds % 60;
        const padded = s < 10 ? `0${s}` : s.toString();
        return `${m}:${padded}`;
    };

    const handleClick = async () => {
        try {
            await onReconfirm(reservationId);
            setStatus("CONFIRMED");
        } catch (err) {
            console.error("재확정 실패", err);
            alert("재확정에 실패했습니다.");
        }
    };

    // 재확정 완료 상태
    if (status === "CONFIRMED") {
        return <span className="text-green-600 font-medium">예매완료</span>;
    }

    // TTL 만료
    if (timeLeft <= 0) {
        return <span className="text-gray-400">재확정 불가 (만료)</span>;
    }

    return (
        <div className="flex items-center gap-2">
            <AlertDialog>
                <AlertDialogTrigger asChild>
                    <Button className="bg-yellow-400 hover:bg-yellow-500 text-black font-semibold">
                        🔄 재확정
                    </Button>
                </AlertDialogTrigger>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>예매 재확정</AlertDialogTitle>
                        <AlertDialogDescription>
                            이 예매는 취소 상태입니다. <br />
                            재확정은 <span className="text-red-500">단 한 번만</span> 가능하며,
                            남은 시간 {formatTime(timeLeft)} 내에만 실행할 수 있습니다.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel>취소</AlertDialogCancel>
                        <AlertDialogAction onClick={handleClick}>
                            확인
                        </AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>

            <span className="text-sm text-gray-500">
        남은 시간 {formatTime(timeLeft)}
      </span>
        </div>
    );
}
