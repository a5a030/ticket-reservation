import { useEffect, useState } from "react";

type Summary = {
    totalRevenue: number;
    totalCount: number;
    averageAmount: number;
};

export default function TotalSummaryCards() {
    const [summary, setSummary] = useState<Summary | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetch("/admin/payments/statistics/summary")
            .then((res) => {
                if (!res.ok) throw new Error("요약 데이터를 불러올 수 없습니다.");
                return res.json();
            })
            .then((json) => setSummary(json))
            .catch((err) => setError(err.message));
    }, []);

    if (error) return <div className="text-red-500">{error}</div>;

    if (!summary) {
        // ✅ 스켈레톤 로딩
        return (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 animate-pulse">
                <div className="h-24 bg-gray-200 rounded-2xl"></div>
                <div className="h-24 bg-gray-200 rounded-2xl"></div>
                <div className="h-24 bg-gray-200 rounded-2xl"></div>
            </div>
        );
    }

    const cards = [
        { title: "💰 총 매출액", value: summary.totalRevenue, color: "bg-indigo-600", unit: "원" },
        { title: "🧾 총 결제 건수", value: summary.totalCount, color: "bg-green-600", unit: "건" },
        { title: "🎟️ 평균 결제 금액", value: summary.averageAmount, color: "bg-yellow-500", unit: "원" },
    ];

    return (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {cards.map((card, idx) => (
                <div
                    key={idx}
                    className={`p-6 ${card.color} text-white rounded-2xl shadow flex flex-col items-center justify-center`}
                >
                    <h2 className="text-lg font-semibold">{card.title}</h2>
                    <p className="text-3xl font-bold mt-2">
                        {card.value.toLocaleString()} {card.unit}
                    </p>
                </div>
            ))}
        </div>
    );
}
