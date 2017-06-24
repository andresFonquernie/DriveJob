package es.fonkyprojects.drivejob.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.activity.R;
import es.fonkyprojects.drivejob.model.Car;

public class CarViewAdapter extends RecyclerView.Adapter<CarViewAdapter.CarHolder> {

    private List<Car> data = new ArrayList<>();
    private final CarViewAdapter.OnDeleteClickListener listenerDelete;

    public interface OnDeleteClickListener {
        void OnDeleteClick(Car item);
    }

    public CarViewAdapter(List<Car> data, CarViewAdapter.OnDeleteClickListener listenerDelete) {
        this.data = data;
        this.listenerDelete = listenerDelete;
    }

    @Override
    public CarViewAdapter.CarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_listcar, parent, false);
        return new CarHolder(view);
    }

    @Override
    public void onBindViewHolder(CarViewAdapter.CarHolder holder, int position) {

        Car car = data.get(position);
        holder.txtCarDetail.setText(car.toString());
        holder.bindToDelete(data.get(position), listenerDelete);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    static class CarHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.carDetail) TextView txtCarDetail;
        @BindView(R.id.btnDeleteCar) ImageButton btnDelete;

        CarHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindToDelete(final Car car, final CarViewAdapter.OnDeleteClickListener listener) {
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnDeleteClick(car);
                }
            });
        }
    }

}
