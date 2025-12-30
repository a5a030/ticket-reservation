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

function TopList({
                     title,
                     items,
                     total,
                     color,
                 }: {
    title: string;
    items: SalesStats[];
    total: number;
    color: string;
}) {
    return (
        <div className="p-4 bg-white rounded-2xl shadow">
            <h2 className="text-lg font-bold mb-4">{title}</h2>
            <ul className="space-y-4">
                {items.map((item, idx) => {
                    const percentage = total > 0 ? (item.totalAmount / total) * 100 : 0;
                    return (
                        <li key={idx}>
                            <div className="flex justify-between mb-1 text-sm">
                <span>
                  {idx + 1}. {item.label}
                </span>
                                <span>
                  {item.totalAmount.toLocaleString()} 원 (
                                    {percentage.toFixed(1)}%)
                </span>
                            </div>
                            <div className="w-full bg-gray-200 rounded-full h-2">
                                <div
                                    className={`${color} h-2 rounded-full`}
                                    style={{ width: `${percentage}%` }}
                                ></div>
                            </div>
                        </li>
                    );
                })}
            </ul>
        </div>
    );
}

export default function DashboardCards() {
    const [data, setData] = useState<DashboardCardsData | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetch("/admin/payments/statistics/cards")
            .then((res) => res.json())
            .then((json) => setData(json))
            .catch(() => setError("데이터를 불러오는 데 실패했습니다."));
    }, []);

    if (error) return <div className="text-red-500">{error}</div>;
    if (!data) return <div>Loading...</div>;

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
            <TopList
                title="🔥 매출 TOP 3 공연"
                items={data.topPerformances}
                total={totalPerformanceAmount}
                color="bg-indigo-500"
            />
            <TopList
                title="🎶 매출 TOP 3 장르"
                items={data.topGenres}
                total={totalGenreAmount}
                color="bg-green-500"
            />
        </div>
    );
}
