import ReconfirmButton from "@/components/ReconfirmButton";

interface ReservationCardProps {
  id: number;
  title: string;
  seat: string;
  date: string;
  status: "CONFIRMED" | "CANCELLED" | "EXPIRED";
  ttlSeconds?: number;
}

export default function ReservationCard({
                                          id,
                                          title,
                                          seat,
                                          date,
                                          status,
                                          ttlSeconds = 0,
                                        }: ReservationCardProps) {
  const handleReconfirm = async (reservationId: number) => {
    const res = await fetch(`/api/reservations/${reservationId}/reconfirm`, {
      method: "PATCH",
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    if (!res.ok) throw new Error("재확정 실패");
  };

  return (
      <div className="border rounded-xl p-4 shadow-sm flex flex-col gap-2 bg-white">
        <h3 className="text-lg font-semibold">{title}</h3>
        <p className="text-gray-600">{seat}</p>
        <p className="text-gray-500 text-sm">{date}</p>

        <div className="flex justify-between items-center mt-2">
          {status === "CONFIRMED" && (
              <span className="text-green-600 font-semibold">예매완료</span>
          )}

          {status === "CANCELLED" && (
              <ReconfirmButton
                  reservationId={id}
                  initialStatus={status}
                  ttlSeconds={ttlSeconds}
                  onReconfirm={handleReconfirm}
              />
          )}

          {status === "EXPIRED" && (
              <span className="text-gray-400 font-medium">
            재확정 불가 (만료됨)
          </span>
          )}
        </div>
      </div>
  );
}