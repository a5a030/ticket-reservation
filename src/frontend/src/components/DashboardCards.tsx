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
        fetch("/admin/dashboard/cards")
            .then((res) => res.json())
            .then((json) => setData(json));
    }, []);

    if (!data) return <div>Loading...</div>;

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* 공연 TOP3 */}
            <div className="p-4 bg-white rounded-2xl shadow">
                <h2 className="text-lg font-bold mb-4">🔥 매출 TOP 3 공연</h2>
                <ul className="space-y-2">
                    {data.topPerformances.map((item, idx) => (
                        <li key={idx} className="flex justify-between">
                            <span>{idx + 1}. {item.label}</span>
                            <span className="font-semibold">{item.totalAmount.toLocaleString()} 원</span>
                        </li>
                    ))}
                </ul>
            </div>

            {/* 장르 TOP3 */}
            <div className="p-4 bg-white rounded-2xl shadow">
                <h2 className="text-lg font-bold mb-4">🎶 매출 TOP 3 장르</h2>
                <ul className="space-y-2">
                    {data.topGenres.map((item, idx) => (
                        <li key={idx} className="flex justify-between">
                            <span>{idx + 1}. {item.label}</span>
                            <span className="font-semibold">{item.totalAmount.toLocaleString()} 원</span>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
}
