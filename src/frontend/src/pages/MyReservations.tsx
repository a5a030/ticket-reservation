import { useEffect, useState } from "react";
import ReservationCard from "@/components/ReservationCard";

interface Reservation {
    id: number;
    title: string;
    seat: string;
    date: string;
    status: "CONFIRMED" | "CANCELLED";
    ttlSeconds?: number;
}

export default function MyReservations() {
    const [reservations, setReservations] = useState<Reservation[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchReservations = async () => {
            try {
                const res = await fetch("/api/reservations/my?sort=recent", {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`,
                    },
                });

                if (!res.ok) {
                    throw new Error("예약 목록을 불러오지 못했습니다.");
                }

                const data = await res.json();

                // 🔥 백엔드 ReservationResponse 매핑
                const mapped = data.map((r: any) => ({
                    id: r.id,
                    title: r.performanceTitle, // ReservationResponse DTO에 맞춰 조정 필요
                    seat: r.seatNo,
                    date: r.createdAt,
                    status: r.status,
                    ttlSeconds: r.ttlSeconds ?? 0, // 백엔드에서 TTL 내려주는 경우
                }));

                setReservations(mapped);
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchReservations();
    }, []);

    if (loading) {
        return <p className="text-center text-gray-500">불러오는 중...</p>;
    }

    if (reservations.length === 0) {
        return <p className="text-center text-gray-500">예매 내역이 없습니다.</p>;
    }

    return (
        <div className="max-w-2xl mx-auto p-4 grid gap-4">
            <h2 className="text-xl font-bold mb-2">내 예매 내역</h2>
            {reservations.map((r) => (
                <ReservationCard key={r.id} {...r} />
            ))}
        </div>
    );
}
