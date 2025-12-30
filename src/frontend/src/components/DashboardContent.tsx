import React, { useEffect, useState } from "react";
import axios from "axios";
import SalesSection from "./SalesSection";
import SalesByCategorySection from "./SalesByCategorySection";
import ReviewSection from "./ReviewSection";
import TicketSection from "./TicketSection";
import { DashboardResponse, PaymentSalesStats } from "../../types/dashboard";

const DashboardContent: React.FC = () => {
    const [data, setData] = useState<DashboardResponse | null>(null);
    const [salesByPerformance, setSalesByPerformance] = useState<PaymentSalesStats[]>([]);
    const [salesByGenre, setSalesByGenre] = useState<PaymentSalesStats[]>([]);

    useEffect(() => {
        Promise.all([
            axios.get("/admin/dashboard", {
                params: { start: "2025-08-01T00:00:00", end: "2025-09-01T00:00:00" }
            }),
            axios.get("/admin/payments/statistics/performance"),
            axios.get("/admin/payments/statistics/genre")
        ])
            .then(([dashboardRes, perfRes, genreRes]) => {
                setData(dashboardRes.data);
                setSalesByPerformance(perfRes.data);
                setSalesByGenre(genreRes.data);
            })
            .catch(err => console.error("대시보드 로딩 실패:", err));
    }, []);


    if (!data) return <p>Loading...</p>;

    return (
        <div className="space-y-6">
            <SalesSection sales={data.sales} />
            <SalesByCategorySection
                performanceData={salesByPerformance}
                genreData={salesByGenre}
            />
            <ReviewSection data={data.reviews} />
            <TicketSection data={data.tickets} />
        </div>
    );

};

export default DashboardContent;