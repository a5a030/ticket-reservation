import { useEffect, useState } from "react";

type Summary = {
    totalRevenue: number;
    totalCount: number;
    averageAmount: number;
};

export default function TotalSummaryCards() {
    const [summary, setSummary] = useState<Summary | null>(null);

    useEffect(() => {
        fetch("/admin/payments/statistics/summary")
            .then((res) => res.json())
            .then((json) => setSummary(json));
    }, []);

    if (!summary) return <div>Loading...</div>;

    return (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="p-6 bg-indigo-600 text-white rounded-2xl shadow flex flex-col items-center justify-center">
                <h2 className="text-lg font-semibold">💰 총 매출액</h2>
                <p className="text-3xl font-bold mt-2">
                    {summary.totalRevenue.toLocaleString()} 원
                </p>
            </div>
            <div className="p-6 bg-green-600 text-white rounded-2xl shadow flex flex-col items-center justify-center">
                <h2 className="text-lg font-semibold">🧾 총 결제 건수</h2>
                <p className="text-3xl font-bold mt-2">
                    {summary.totalCount.toLocaleString()} 건
                </p>
            </div>
            <div className="p-6 bg-yellow-500 text-white rounded-2xl shadow flex flex-col items-center justify-center">
                <h2 className="text-lg font-semibold">🎟️ 평균 결제 금액</h2>
                <p className="text-3xl font-bold mt-2">
                    {summary.averageAmount.toLocaleString()} 원
                </p>
            </div>
        </div>
    );
}
