import { useEffect, useState } from "react";

type SalesStats = {
    label: string;
    totalAmount: number;
    count: number;
};

type DashboardCardsData = {
    topPerformances: SalesStats[];
    topGenres: SalesStats[];
};

export default function DashboardCards() {
    const [data, setData] = useState<DashboardCardsData | null>(null);

    useEffect(() => {
        fetch("/admin/payments/statistics/cards")
            .then((res) => res.json())
            .then((json) => setData(json));
    }, []);

    if (!data) return <div>Loading...</div>;

    // 총합 계산 → 점유율 구할 때 사용
    const totalPerformanceAmount = data.topPerformances.reduce(
        (sum, item) => sum + item.totalAmount,
        0
    );
    const totalGenreAmount = data.topGenres.reduce(
        (sum, item) => sum + item.totalAmount,
        0
    );

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* 공연 TOP3 */}
            <div className="p-4 bg-white rounded-2xl shadow">
                <h2 className="text-lg font-bold mb-4">🔥 매출 TOP 3 공연</h2>
                <ul className="space-y-4">
                    {data.topPerformances.map((item, idx) => {
                        const percentage =
                            totalPerformanceAmount > 0
                                ? (item.totalAmount / totalPerformanceAmount) * 100
                                : 0;
                        return (
                            <li key={idx}>
                                <div className="flex justify-between mb-1 text-sm">
                                    <span>{idx + 1}. {item.label}</span>
                                    <span>
                    {item.totalAmount.toLocaleString()} 원 ({percentage.toFixed(1)}%)
                  </span>
                                </div>
                                <div className="w-full bg-gray-200 rounded-full h-2">
                                    <div
                                        className="bg-indigo-500 h-2 rounded-full"
                                        style={{ width: `${percentage}%` }}
                                    ></div>
                                </div>
                            </li>
                        );
                    })}
                </ul>
            </div>

            {/* 장르 TOP3 */}
            <div className="p-4 bg-white rounded-2xl shadow">
                <h2 className="text-lg font-bold mb-4">🎶 매출 TOP 3 장르</h2>
                <ul className="space-y-4">
                    {data.topGenres.map((item, idx) => {
                        const percentage =
                            totalGenreAmount > 0
                                ? (item.totalAmount / totalGenreAmount) * 100
                                : 0;
                        return (
                            <li key={idx}>
                                <div className="flex justify-between mb-1 text-sm">
                                    <span>{idx + 1}. {item.label}</span>
                                    <span>
                    {item.totalAmount.toLocaleString()} 원 ({percentage.toFixed(1)}%)
                  </span>
                                </div>
                                <div className="w-full bg-gray-200 rounded-full h-2">
                                    <div
                                        className="bg-green-500 h-2 rounded-full"
                                        style={{ width: `${percentage}%` }}
                                    ></div>
                                </div>
                            </li>
                        );
                    })}
                </ul>
            </div>
        </div>
    );
}
