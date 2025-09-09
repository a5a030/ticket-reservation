import PerformanceSalesChart from "../components/PerformanceSalesChart";
import GenreSalesChart from "../components/GenreSalesChart";
import DashboardCards from "../components/DashboardCards";
import TotalSummaryCards from "../components/TotalSummaryCards";

export default function AdminDashboard() {
    return (
        <div className="space-y-6">
            <TotalSummaryCards />
            <DashboardCards />
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <PerformanceSalesChart />
                <GenreSalesChart />
            </div>
        </div>
    );
}
