import PerformanceRevenueChart from "../components/PerformanceRevenueChart";
import GenreRevenueChart from "../components/GenreRevenueChart";

export default function AdminDashboard() {
    return (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <PerformanceRevenueChart />
            <GenreRevenueChart />
        </div>
    );
}
