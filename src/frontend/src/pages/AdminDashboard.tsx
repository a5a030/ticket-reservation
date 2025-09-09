import PerformanceSalesChart from "../components/PerformanceSalesChart";
import GenreSalesChart from "../components/GenreSalesChart";

export default function AdminDashboard() {
    return (
        <div className="space-y-6">
            <DashboardCards />
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <PerformanceSalesChart />
                <GenreSalesChart />
            </div>
        </div>
    );
}
