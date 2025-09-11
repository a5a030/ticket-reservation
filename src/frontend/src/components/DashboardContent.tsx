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
        axios.get("/admin/dashboard", {
            params: {
                start: "2025-08-01T00:00:00",
                end: "2025-09-01T00:00:00"
            }
        }).then(res => setData(res.data));

        axios.get("/admin/payments/sales/performance").then(res => setSalesByPerformance(res.data));
        axios.get("/admin/payments/sales/genre").then(res => setSalesByGenre(res.data));
    }, []);

    if (!data) return <p>Loading...</p>;

    return (
        <div>
            <SalesSection data={data.sales} />
            <SalesByCategorySection performanceData={salesByPerformance} genreData={salesByGenre} />
            <ReviewSection data={data.reviews} />
            <TicketSection data={data.tickets} />
        </div>
    );
};

export default DashboardContent;